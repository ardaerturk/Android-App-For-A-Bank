package com.b07.bankofjarm.databasehelper;

import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.exceptions.ConnectionFailedException;
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class SerializableDatabase implements Serializable {

  private static final long serialVersionUID = 3954181493952538617L;
  private LinkedHashMap<User, String> usersAndPasswords;
  private LinkedHashMap<String, BigDecimal> accountTypesAndInterestRates;
  private List<String> roles;
  private List<List<String>> messages;

  private final static String DATABASE_NAME = "database_copy.ser";
  private final static String DATABASE_BACKUP_NAME = "database_backup.ser";

  public SerializableDatabase() {
    this.usersAndPasswords = new LinkedHashMap<>();
    this.accountTypesAndInterestRates = new LinkedHashMap<>();
    this.roles = new ArrayList<>();
    this.messages = new ArrayList<>();
  }

  public SerializableDatabase getDatabase() {
    return this;
  }

  public List<String> getRoles() {
    return roles;
  }

  public HashMap<User, String> getUsersAndPasswords() {
    return usersAndPasswords;
  }

  public HashMap<String, BigDecimal> getAccountTypesAndInterestRates() {
    return accountTypesAndInterestRates;
  }

  /**
   * Exports a serialized version of the database to the filename specified.
   *
   * @param name
   * @return true if the database was exported
   */
  private boolean exportDatabaseWithName(File filesDir, String name) {
    File file = null;
    FileOutputStream fout = null;
    ObjectOutputStream oos = null;
    DatabaseSelectHelper instance  = null;
    try {
      instance = DatabaseSelectHelper.getInstance(null);
      List<User> allUsers = instance.getAllUsers();

      // Add passwords as a user-password map
      for (User user : allUsers) {
        String password = instance.getPassword(user.getId());
        this.usersAndPasswords.put(user, password);
        List<String> msgList =  instance.getAllMessages(user.getId(), true);
        this.messages.add(msgList);
      }

      // Add roles and account types
      for (int roleId : instance.getRolesList()) {
        this.roles.add(instance.getRole(roleId));
      }

      for (int accountTypeId : instance.getAccountTypesIdList()) {
        String accountTypeName = instance.getAccountTypeName(accountTypeId);
        BigDecimal interestRate = instance.getInterestRate(accountTypeId);

        this.accountTypesAndInterestRates.put(accountTypeName, interestRate);
      }

      // Export to file
      file = new File(filesDir, DATABASE_NAME);
      fout = new FileOutputStream(file);
      oos = new ObjectOutputStream(fout);
      oos.writeObject(this);
      oos.close();
      fout.close();
    } catch (SQLException | IOException e) {
      return false;
    } finally {
      if (instance != null) {
        instance.close();
      }
    }
    return true;

  }

  /**
   * Exports a serialized version of the database to database_copy.ser.
   * @return true if the database was exported
   */
  public boolean exportDatabase(File filesDir) {
    return this.exportDatabaseWithName(filesDir, DATABASE_NAME);
  }

  public List<List<String>> getMessages() {
    return messages;
  }

  /**
   * Import database from database_copy.ser. Exports a copy of the current database before
   * @return
   */
  public boolean importDatabase(File filesDir) {
    // Import database from file
    DatabaseInsertHelper instance = null;
    try {
      File db = new File(filesDir, DATABASE_NAME);
      FileInputStream fis = new FileInputStream(db);
      ObjectInputStream ois = new ObjectInputStream(fis);
      SerializableDatabase newDatabase = (SerializableDatabase) ois.readObject();
      // Check the roles and account types match with enums in project
      if (!sameRoles(newDatabase.getRoles()) || !sameAccountTypes(new ArrayList<>(newDatabase.getAccountTypesAndInterestRates().keySet()))) {
        return false;
      }

      // Export rollback
      boolean success = this.exportDatabaseWithName(filesDir, DATABASE_BACKUP_NAME);
      if (!success) {
        return false;
      }

      // Clear database
      InitializeDatabase initializeDatabase = InitializeDatabase.getInstance(null);
      initializeDatabase.clearDatabase();

      instance = DatabaseInsertHelper.getInstance(null);
      // Insert roles and accounttypes
      for (String role : newDatabase.getRoles()) {
        instance.insertNewRole(role);
      }

      HashMap<String, BigDecimal> newAccountTypes = newDatabase.getAccountTypesAndInterestRates();

      for (String accountType : newAccountTypes.keySet()) {
        instance.insertNewAccountType(accountType, newAccountTypes.get(accountType));
      }

      // Add user data
      HashMap<User, String> newUsers = newDatabase.getUsersAndPasswords();

      int count = 0;

      for (User user : newUsers.keySet()) {
        String name = user.getName();
        int age = user.getAge();
        String address = user.getAddress();
        int roleId = RolesMap.getInstance(null).getId(user.getRole());
        String password = newUsers.get(user);

        int userId = instance.insertUser(name, age, address, roleId, null);
        if (userId == -1) {
          System.out.println("Not added: " + name);
        }

        DatabaseUpdateHelper instance2 = DatabaseUpdateHelper.getInstance(null);
        boolean updated = instance2.updateUserPassword(password, userId);
        instance2.close();
        if (!updated) {
          System.out.println("Password not added: " + name);
        }

        // Add messages
        for (String msg: newDatabase.getMessages().get(count)) {
          instance.insertNewMessage(userId, msg);
        }

        // Add accounts
        if (user.getRole() == Roles.CUSTOMER) {
          for (Account account : ((Customer)user).getAccounts()) {
            String accName = account.getName();
            BigDecimal balance = account.getBalance();
            int typeId = AccountTypesMap.getInstance(null).getId(account.getTypeEnum());

            int accId = instance.insertNewAccount(accName, balance, typeId);

            if (accId != -1) {
              instance.insertNewUserAccount(userId, accId);
            }

          }
        }

        count++;
      }
    } catch (IOException | ClassNotFoundException e) {
      return false;
    } finally {
      if (instance != null) {
        instance.close();
      }
    }
    return true;
  }

  /**
   * Returns true if the given list of strings contains exactly the same names
   * as the Roles enum in the project.
   *
   * @param newRoles
   * @return true if same values
   */
  private boolean sameRoles(List<String> newRoles) {
    if (newRoles.size() != Roles.values().length) {
      return false;
    }

    List<String> oldRoles = new ArrayList<>();
    for (Roles role : Roles.values()) {
      oldRoles.add(role.name());
    }

    // Sort
    Collections.sort(oldRoles);
    Collections.sort(newRoles);
    return oldRoles.equals(newRoles);
  }

  /**
   * Returns true if the given list of strings contains exactly the same names
   * as the AccountTypes enum in the project.
   *
   * @param newAccountTypes
   * @return true if same values
   */
  private boolean sameAccountTypes(List<String> newAccountTypes) {
    if (newAccountTypes.size() != AccountTypes.values().length) {
      return false;
    }

    List<String> oldAccountTypes = new ArrayList<>();
    for (AccountTypes type : AccountTypes.values()) {
      oldAccountTypes.add(type.name());
    }

    // Sort
    Collections.sort(oldAccountTypes);
    Collections.sort(newAccountTypes);
    return oldAccountTypes.equals(newAccountTypes);
  }

}
