package com.b07.bankofjarm.databasehelper;

import android.content.Context;
import com.b07.bankofjarm.database.DatabaseDriverA;
/*
import com.bank.database.DatabaseInsertException;
import com.bank.database.DatabaseInserter;
import com.bank.databasemap.AccountTypesMap;
import com.bank.databasemap.RolesMap;
*/
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseInsertHelper extends DatabaseDriverA {

  private static DatabaseInsertHelper singleton = null;
  private Context context;

  private DatabaseInsertHelper(Context context) {
    super(context);
    this.context = context;
  }

  /**
   * Returns an instance of the insert helper.
   * @return the instance singleton
   */
  public static DatabaseInsertHelper getInstance(Context context) {
    if (singleton == null) {
      singleton = new DatabaseInsertHelper(context);
    }
    return singleton;
  }

  /**
   * Check if the given roleId corresponds to valid role in database
   *
   * @param roleId is the roleId to check
   * @return whether the roleId is found in the database
   */
  private boolean validRoleId(int roleId) {
    // Iterate through the valid role IDs and see if there is a match
    RolesMap map = RolesMap.getInstance(this.context);
    for (Roles role : Roles.values()) {
      if (roleId == map.getId(role)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if the given role name is valid
   *
   * @param name is the name to check
   * @return whether or not the name is found in the Roles enum
   */
  private static boolean validRole(String name) {
    // Iterate through the valid role IDs and see if there is a match
    for (Roles role : Roles.values()) {
      if (role.toString().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the given account name is valid
   *
   * @param name is the name to check
   * @return whether or not the name is found in the AccountTypes enum
   */
  private static boolean validAccountType(String name) {
    // Iterate through the account types and see if there is a match
    for (AccountTypes account : AccountTypes.values()) {
      if (account.name().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Insert an account into the database
   *
   * @param name is the name of the account to insert
   * @param balance is the balance of the account, which must be >= 0 to start
   * @param typeId is the account type ID
   * @return the account ID of the new account if created successfully, -1 otherwise
   */
  public int insertNewAccount(String name, BigDecimal balance, int typeId) {
    // Get a list of all the valid Account Type IDs
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(this.context);
    List<Integer> validAccountIds = instance.getAccountTypesIdList();
    instance.close();

    if (!validAccountIds.contains(typeId) || balance.compareTo(BigDecimal.ZERO) < 0) {
      return -1;
    }

    return (int) super.insertAccount(name, balance, typeId);
  }

  /**
   * Insert a new account type to the database
   *
   * @param name is the account type name
   * @param interestRate is the interest rate of the account type
   * @return whether or not the account type was successfully added
   */
  public int insertNewAccountType(String name, BigDecimal interestRate) {
    // Check to see if the account type is valid
    boolean nameValid = DatabaseInsertHelper.validAccountType(name);

    // If the interest rate is between 1 and 0, add account type to the database
    if (interestRate.compareTo(BigDecimal.ONE) < 0 &&
        interestRate.compareTo(BigDecimal.ZERO) >= 0 &&
        nameValid) {
      return (int) super.insertAccountType(name, interestRate);
    }

    return -1;
  }

  /**
   * Insert a new user to the database
   *
   * @param name is the name of the user
   * @param age is the user's age
   * @param address is the user's address
   * @param roleId is the user's roleId
   * @param password is the user's password
   * @return the new user's id if created successfully, -1 otherwise
   */
  public int insertUser(String name, int age, String address, int roleId, String password) {
    // Check if the address is within 100 characters
    if (address.length() > 100 || !this.validRoleId(roleId) || age <= 0) {
      return -1;
    }

    return (int)super.insertNewUser(name, age, address, roleId, password);
  }

  /**
   * Insert a new role to the database
   *
   * @param role is the name of the role
   * @return -1 if role was not added, id of role otherwise
   */
  public int insertNewRole(String role) {
    // Check to see if the given role is valid
    if (!DatabaseInsertHelper.validRole(role)) {
      return -1;
    }
    // Check to see if role already exists in database
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(this.context);
    List<Integer> roleIds = instance.getRolesList();
    instance.close();

    for (int roleId : roleIds) {
      if (instance.getRole(roleId).equalsIgnoreCase(role)) {
        return -1;
      }
    }

    // If the role name is valid, add the role to the database
    return (int) super.insertRole(role);
  }

  /**
   * Insert new UserAcccount relationship
   *
   * @param userId is the user id to connect with account
   * @param accountId is the account id to connect with user
   * @return -1 if UserAccount not added, id of UserAccount otherwise
   */
  public int insertNewUserAccount(int userId, int accountId) {
    // Get all the account ids associated to the given user id
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(this.context);
    // Check to see if the given account id is not already in the list of accounts
    for (int id : instance.getAccountIdsList(userId)) {
      if (id == accountId) {
        instance.close();
        return -1;
      }
    }
    instance.close();
    return (int) super.insertUserAccount(userId, accountId);
  }

  /**
   * Delivers a new message to a user.
   * @param userId is the ID of the user to which you wish to send the message.
   * @param message is the message you wish to send.
   * @return -1 if the message could not be delivered, otherwise the message's ID.
   */
  public int insertNewMessage(int userId, String message) {
    // Check message length
    if (message.length() >= 512) {
      return -1;
    }

    return (int) super.insertMessage(userId, message);
  }
}
