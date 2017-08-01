package com.bank.databasehelper;

import com.bank.database.DatabaseInsertException;
import com.bank.database.DatabaseInserter;
import com.bank.databasemap.AccountTypesMap;
import com.bank.databasemap.RolesMap;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseInsertHelper extends DatabaseInserter {

  /**
   * Check if the given roleId corresponds to valid role in database
   *
   * @param roleId is the roleId to check
   * @return whether the roleId is found in the database
   * @throws SQLException if there is a problem recovering roles from the database
   */
  private static boolean validRoleId(int roleId) throws SQLException {
    // Iterate through the valid role IDs and see if there is a match
    for (Roles role : Roles.values()) {
      if (roleId == RolesMap.getId(role)) {
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
    boolean nameValid = false;
    // Iterate through the valid role IDs and see if there is a match
    for (Roles role : Roles.values()) {
      if (role.toString().equalsIgnoreCase(name)) {
        nameValid = true;
      }
    }
    return nameValid;
  }

  /**
   * Check if the given account name is valid
   *
   * @param name is the name to check
   * @return whether or not the name is found in the AccountTypes enum
   */
  private static boolean validAccountType(String name) {
    boolean nameValid = false;
    // Iterate through the account types and see if there is a match
    for (AccountTypes account : AccountTypes.values()) {
      if (account.toString().equalsIgnoreCase(name)) {
        nameValid = true;
      }
    }
    return nameValid;
  }

  /**
   * Insert an account into the database
   *
   * @param name is the name of the account to insert
   * @param balance is the balance of the account, which must be >= 0 to start
   * @param typeId is the account type ID
   * @return the account ID of the new account if created successfully, -1 otherwise
   * @throws SQLException if there is a problem with the database
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId)
      throws SQLException {
    // Get a list of all the valid Account Type IDs
    List<Integer> validAccountIds = DatabaseSelectHelper.getAccountTypesIds();
    // Create a variable of the return value
    int toReturn;
    // Check if the account type is balance owing
    boolean balanceOwingAccount = (typeId == AccountTypesMap.getId(AccountTypes.BALANCE_OWING));
    // Check if the given balance value is negative
    boolean negativeBalance = (balance.compareTo(new BigDecimal(0)) == -1);
    // If the typeId is valid and the balance is positive, add the account
    if (validAccountIds.contains(typeId) && (balanceOwingAccount || !negativeBalance)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        toReturn = DatabaseInserter.insertAccount(name, balance, typeId, connection);
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    } else {
      // If the account type ID does not exist, return -1
      toReturn = -1;
    }
    return toReturn;
  }

  /**
   * Insert a new account type to the database
   *
   * @param name is the account type name
   * @param interestRate is the interest rate of the account type
   * @return whether or not the account type was successfully added
   * @throws SQLException if there is a problem with the database
   */
  public static int insertAccountType(String name, BigDecimal interestRate)
      throws SQLException {
    // Check to see if the account type is valid
    boolean nameValid = DatabaseInsertHelper.validAccountType(name);
    double intInterestRate = interestRate.doubleValue();
    int toReturn;
    // If the interest rate is between 1 and 0, add account type to the database
    if ((1 > intInterestRate) && (intInterestRate >= 0) && nameValid) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        toReturn = DatabaseInserter.insertAccountType(name, interestRate, connection);
        AccountTypesMap.update();
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    } else {
      // If either the interest rate or name is invalid, return false
      toReturn = -1;
    }
    return toReturn;
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
   * @throws SQLException if there is a problem accessing the database
   */
  public static int insertNewUser(String name, int age, String address, int roleId, String password)
      throws SQLException {
    // Check if the address is within 100 characters
    boolean addressCharLimit = false;
    if (address.length() <= 100) {
      addressCharLimit = true;
    }
    // Check if the given role id is valid
    boolean roleValid = DatabaseInsertHelper.validRoleId(roleId);
    // Check if the age is above 0
    boolean ageValid = (age > 0);
    // Create a variable for the userId and set it to be -1 by default
    int userId = -1;
    // If all the conditions are met, add the user to the database and return their Id
    if (addressCharLimit && roleValid && ageValid) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        userId = DatabaseInserter.insertNewUser(name, age, address, roleId, password, connection);
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    }
    return userId;
  }

  /**
   * Insert a new role to the database
   *
   * @param role is the name of the role
   * @return -1 if role was not added, id of role otherwise
   * @throws SQLException if there was a problem with the database
   */
  public static int insertRole(String role) throws SQLException {
    // Check to see if the given role is valid
    boolean nameValid = DatabaseInsertHelper.validRole(role);
    // Check to see if role already exists in database
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    // Loop through the roleIds to see if Customer exists in them
    Boolean roleExists = false;
    for (Integer roleId : roleIds) {
      if (DatabaseSelectHelper.getRole(roleId).equalsIgnoreCase(role)) {
        roleExists = true;
      }
    }
    // If the role name is valid, add the role to the database
    int toReturn;
    if (nameValid && roleExists.equals(false)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        toReturn = DatabaseInserter.insertRole(role, connection);
        RolesMap.update();
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    } else if (roleExists.equals(true) && nameValid) {
      // If the role exists already, return -1
      toReturn = -1;
    } else {
      // If the name is not valid, return -1
      toReturn = -1;
    }
    return toReturn;
  }

  /**
   * Insert new UserAcccount relationship
   *
   * @param userId is the user id to connect with account
   * @param accountId is the account id to connect with user
   * @return -1 if UserAccount not added, id of UserAccount otherwise
   * @throws SQLException
   */
  public static int insertUserAccount(int userId, int accountId)
            throws SQLException {
    // Return if ids are less than 1
    if (userId <= 0 || accountId <= 0) {
      return -1;
    }
    // Get all the account ids associated to the given user id
    List<Integer> accountIds = DatabaseSelectHelper.getAccountIds(userId);
    // Check to see if the given account id is not already in the list of accounts
    boolean uniqueAccount = true;
    for (int id : accountIds) {
      if (id == accountId) {
        uniqueAccount = false;
      }
    }
    // If the UserAccount doesnt already exist, add it to database
    int toReturn;
    if (uniqueAccount) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        toReturn = DatabaseInserter.insertUserAccount(userId, accountId, connection);
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    } else {
      // If the UserAccount already exists, return false
      toReturn = -1;
    }
    return toReturn;
  }

  /**
   * Delivers a new message to a user.
   * @param userId is the ID of the user to which you wish to send the message.
   * @param message is the message you wish to send.
   * @return -1 if the message could not be delivered, otherwise the message's ID.
   * @throws SQLException
   */
  public static int insertMessage(int userId, String message) throws SQLException {
    int messageId;
    if (message.length() < 521) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        messageId = DatabaseInserter.insertMessage(userId, message, connection);
        return messageId;
      } catch (DatabaseInsertException e) {
        return -1;
      } finally {
        connection.close();
      }
    } else {
      // If the message was to long.
      return -1;
    }
  }
}
