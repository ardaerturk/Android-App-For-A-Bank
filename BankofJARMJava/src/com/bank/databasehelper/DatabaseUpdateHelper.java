package com.bank.databasehelper;

import com.bank.database.DatabaseUpdater;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseUpdateHelper extends DatabaseUpdater {

  /**
   * Update the role name of the given role
   *
   * @param name is the new name of the role
   * @param id is the role's ID
   * @return whether or not the role was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateRoleName(String name, int id) throws SQLException {
    // Check if the given id is greater than or equal to 1, and that the given string is not empty
    Boolean nameEmpty = name.isEmpty();
    boolean complete;
    // Check if the given role name is valid
    boolean validRole = false;
    for (Roles role : Roles.values()) {
      if (role.toString().equalsIgnoreCase(name)) {
        validRole = true;
      }
    }
    if ((id >= 1) && nameEmpty.equals(false) && validRole) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateRoleName(name, id, connection);
      connection.close();
    } else {
      // If the given id is less than 1, it is automatically invalid
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given user's name
   *
   * @param name is the new name
   * @param id is the user's ID
   * @return whether or not the name was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateUserName(String name, int id) throws SQLException {
    // Check if the given id is greater than or equal to 1, and that the given string is not empty
    Boolean nameEmpty = name.isEmpty();
    boolean complete;
    if ((id >= 1) && nameEmpty.equals(false)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateUserName(name, id, connection);
      connection.close();
    } else {
      // If the id is less than 1, it is automatically invalid
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given user's age
   *
   * @param age is the new age
   * @param id is the user's ID
   * @return whether or not the user's age was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateUserAge(int age, int id) throws SQLException {
    // Check to see if the given age is greater than 0, and the id is >= 1
    boolean complete;
    if ((id >= 1) && (age > 0)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateUserAge(age, id, connection);
      connection.close();
    } else {
      // If the age is not possible and the id is not valid, return false
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given user's role ID
   *
   * @param roleId is the new role ID
   * @param id is the user's ID
   * @return whether or not the roleID was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateUserRole(int roleId, int id) throws SQLException {
    // Check to see if the roleId is valid
    List<Integer> validRoles = DatabaseSelectHelper.getRoles();
    boolean complete;
    if (validRoles.contains(roleId)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateUserRole(roleId, id, connection);
      connection.close();
    } else {
      // If the roleId is not valid, return false
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given user's address
   *
   * @param address is the new address
   * @param id is the user's ID
   * @return whether or not the user's address was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateUserAddress(String address, int id) throws SQLException {
    // Check if the id is greater than or equal to 1
    boolean complete;
    if ((id >= 1) && (address.length() > 0)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateUserAddress(address, id, connection);
      connection.close();
    } else {
      // If the address is empty and the id is not valid, return false
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given account's name
   *
   * @param name is the new name
   * @param id is the account's ID
   * @return whether or not the account name was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateAccountName(String name, int id) throws SQLException {
    // Check if the id is >= 1
    boolean complete;
    if ((id >= 1) && (name.length() > 0)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateAccountName(name, id, connection);
      connection.close();
    } else {
      // If the address is empty and the id is invalid, return false
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given account's balance
   *
   * @param balance is the new balance
   * @param id is the account's ID
   * @return whether or not the balance was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id) throws SQLException {
    // Check if the id is >= 1
    boolean complete;
    if (id >= 1) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateAccountBalance(balance, id, connection);
      connection.close();
    } else {
      // If the id is invalid, return false
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given account's type
   *
   * @param typeId is the new type
   * @param id is the account's ID
   * @return whether or not the account type was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateAccountType(int typeId, int id) throws SQLException {
    // Check to see if the given account type is valid
    boolean complete;
    List<Integer> accountTypes = DatabaseSelectHelper.getAccountTypesIds();
    if ((id >= 1) && (accountTypes.contains(typeId))) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateAccountType(typeId, id, connection);
      connection.close();
    } else {
      // Return false if the typeId and id are not valid
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given account type's name
   *
   * @param name is the new name
   * @param id is the ID of the account type
   * @return whether or not the account type name was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateAccountTypeName(String name, int id) throws SQLException {
    // Check if the name is valid
    boolean nameValid = false;
    // Iterate through the account types and see if there is a match
    for (AccountTypes account : AccountTypes.values()) {
      if (account.toString().equalsIgnoreCase(name)) {
        nameValid = true;
      }
    }
    // Check if the id is valid
    boolean complete;
    if ((id >= 1) && nameValid) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateAccountTypeName(name, id, connection);
      connection.close();
    } else {
      complete = false;
    }
    return complete;
  }

  /**
   * Update the given account type's interest rate
   *
   * @param interestRate is the new interest rate
   * @param id is the account type's ID
   * @return whether or not the interest rate was successfully updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id)
      throws SQLException {
    // Check if the interest rate is within 0 and 1, and that the id is valid
    int interestRateInt = interestRate.intValueExact();
    boolean complete;
    if ((id >= 1) && (interestRateInt < 1) && (interestRateInt >= 0)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      complete = DatabaseUpdater.updateAccountTypeInterestRate(interestRate, id, connection);
      connection.close();
    } else {
      complete = false;
    }
    return complete;
  }


  /**
   * Updates a user's password in the database with a given hashed password.
   * @param password The password of the user, hashed
   * @param id The id of the user in the database
   * @return true if password was updated
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static boolean updateUserPassword(String password, int id) throws SQLException {
    if (id >= 1) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      return DatabaseUpdater.updateUserPassword(password, id, connection);
    }
    return false;
  }

  /**
   * Changes the viewed status of the message to 1, which we interpret to mean viewed.
   * @param id is the ID of the message
   * @return True if the message was successfully updated, false otherwise.
   * @throws SQLException
   */
  public static boolean updateUserMessageState(int id) throws SQLException {
	boolean complete;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    complete = DatabaseUpdater.updateUserMessageState(id, connection);
    connection.close();
    return complete;
  }
}
