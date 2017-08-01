package com.bank.databasehelper;

import com.bank.account.Account;
import com.bank.bank.DatabaseDriverExtender;
import com.bank.database.DatabaseDriver;
import com.bank.database.DatabaseUpdater;
import com.bank.databasemap.AccountTypesMap;
import com.bank.databasemap.RolesMap;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.security.PasswordHelpers;
import com.bank.user.Customer;
import com.bank.user.User;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private boolean exportDatabaseWithName(String name) {
    FileOutputStream fout = null;
    ObjectOutputStream oos = null;
    try {
      List<User> allUsers = DatabaseSelectHelper.getAllUsers();

      // Add passwords as a user-password map
      for (User user : allUsers) {
        String password = DatabaseSelectHelper.getPassword(user.getId());
        this.usersAndPasswords.put(user, password);
        List<String> msgList =  DatabaseSelectHelper.getAllMessages(user.getId(), true);
        this.messages.add(msgList);
      }

      // Add roles and account types
      for (int roleId : DatabaseSelectHelper.getRoles()) {
        this.roles.add(DatabaseSelectHelper.getRole(roleId));
      }

      for (int accountTypeId : DatabaseSelectHelper.getAccountTypesIds()) {
        String accountTypeName = DatabaseSelectHelper.getAccountTypeName(accountTypeId);
        BigDecimal interestRate = DatabaseSelectHelper.getInterestRate(accountTypeId);

        this.accountTypesAndInterestRates.put(accountTypeName, interestRate);
      }

      // Export to file
      fout = new FileOutputStream(DATABASE_NAME);
      oos = new ObjectOutputStream(fout);
      oos.writeObject(this);
      oos.close();
      fout.close();
    } catch (SQLException | IOException e) {
      return false;
    }
    return true;

  }

  /**
   * Exports a serialized version of the database to database_copy.ser.
   * @return true if the database was exported
   */
  public boolean exportDatabase() {
    return this.exportDatabaseWithName(DATABASE_NAME);
  }

  public List<List<String>> getMessages() {
    return messages;
  }

  /**
   * Import database from database_copy.ser. Exports a copy of the current database before
   * @return
   */
  public boolean importDatabase() {
    // Import database from file
    try {
      FileInputStream fis = new FileInputStream(DATABASE_NAME);
      ObjectInputStream ois = new ObjectInputStream(fis);
      SerializableDatabase newDatabase = (SerializableDatabase) ois.readObject();

      // Check the roles and account types match with enums in project
      if (!sameRoles(newDatabase.getRoles()) || !sameAccountTypes(new ArrayList<>(newDatabase.getAccountTypesAndInterestRates().keySet()))) {
        return false;
      }

      // Export rollback
      boolean success = this.exportDatabaseWithName(DATABASE_BACKUP_NAME);
      if (!success) {
        return false;
      }

      // Clear database
      DatabaseDriverExtender.reInitialize();

      // Insert roles and accounttypes
      for (String role : newDatabase.getRoles()) {
        DatabaseInsertHelper.insertRole(role);
      }

      HashMap<String, BigDecimal> newAccountTypes = newDatabase.getAccountTypesAndInterestRates();

      for (String accountType : newAccountTypes.keySet()) {
        DatabaseInsertHelper.insertAccountType(accountType, newAccountTypes.get(accountType));
      }

      // Add user data
      HashMap<User, String> newUsers = newDatabase.getUsersAndPasswords();

      int count = 0;

      for (User user : newUsers.keySet()) {
        String name = user.getName();
        int age = user.getAge();
        String address = user.getAddress();
        int roleId = RolesMap.getId(user.getRole());
        String password = newUsers.get(user);

        int userId = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, null);
        if (userId == -1) {
          System.out.println("Not added: " + name);
        }

        boolean updated = DatabaseUpdateHelper.updateUserPassword(password, userId);
        if (!updated) {
          System.out.println("Password not added: " + name);
        }

        // Add messages
        for (String msg: newDatabase.getMessages().get(count)) {
          DatabaseInsertHelper.insertMessage(userId, msg);
        }

        // Add accounts
        if (user.getRole() == Roles.CUSTOMER) {
          for (Account account : ((Customer)user).getAccounts()) {
            String accName = account.getName();
            BigDecimal balance = account.getBalance();
            int typeId = AccountTypesMap.getId(account.getTypeEnum());

            int accId = DatabaseInsertHelper.insertAccount(accName, balance, typeId);

            if (accId != -1) {
              DatabaseInsertHelper.insertUserAccount(userId, accId);
            }

          }
        }

        count++;
      }
    } catch (IOException | ClassNotFoundException | SQLException | ConnectionFailedException e) {
      return false;
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

    List<String> oldRoles = Stream.of(Roles.values()).map(Roles::name).collect(Collectors.toList());
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

    List<String> oldAccountTypes = Stream.of(AccountTypes.values()).map(AccountTypes::name).collect(Collectors.toList());
    // Sort
    Collections.sort(oldAccountTypes);
    Collections.sort(newAccountTypes);
    return oldAccountTypes.equals(newAccountTypes);
  }

}
