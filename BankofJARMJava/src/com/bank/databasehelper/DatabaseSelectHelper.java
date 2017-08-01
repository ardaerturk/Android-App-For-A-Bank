package com.bank.databasehelper;

import com.bank.account.Account;
import com.bank.database.DatabaseSelector;
import com.bank.databasemap.AccountTypesMap;
import com.bank.databasemap.RolesMap;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.user.Admin;
import com.bank.user.Customer;
import com.bank.user.Teller;
import com.bank.user.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSelectHelper extends DatabaseSelector {

  /**
   * Build a user with the given info.
   *
   * @param userId is the userId of the user to build
   * @param name is the name of the user
   * @param age is the age of the user
   * @param address is the address of the user
   * @param roleId is the roleId of the user
   * @return a User with the given info
   * @throws SQLException if there is a problem accessing the database
   */
  private static User buildUser(int userId, String name, int age, String address, int roleId)
      throws SQLException {
    // Check which type of user we need to build
    User toReturn;
    if (roleId == RolesMap.getId(Roles.ADMIN)) {
      toReturn = new Admin(userId, name, age, address);
    } else if (roleId == RolesMap.getId(Roles.CUSTOMER)) {
      toReturn = new Customer(userId, name, age, address);
    } else if (roleId == RolesMap.getId(Roles.TELLER)) {
      toReturn = new Teller(userId, name, age, address);
    } else {
      // If the roleId is not valid, the user should not exist in the first place
      toReturn = null;
    }
    return toReturn;
  }

  /**
   * Build an account with the given info.
   *
   * @param accountId is the account id
   * @param name is the account name
   * @param balance is the account balance
   * @param typeId is the account's type ID
   * @return an account with the given info included
   * @throws SQLException if there is a problem accessing the database
   */
  private static Account buildAccount(int accountId, String name, String balance, int typeId)
      throws SQLException {
    // Check which type of account we need to build
    Account toReturn;
    // Convert the string balance to BigDecimal
    BigDecimal bigDecimalBalance = new BigDecimal(balance);
    if (typeId == AccountTypesMap.getId(AccountTypes.CHEQUING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.CHEQUING);
    } else if (typeId == AccountTypesMap.getId(AccountTypes.SAVING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.SAVING);
    } else if (typeId == AccountTypesMap.getId(AccountTypes.TFSA)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.TFSA);
    } else if (typeId == AccountTypesMap.getId(AccountTypes.TFSA)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.TFSA);
    } else if (typeId == AccountTypesMap.getId(AccountTypes.BALANCE_OWING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.BALANCE_OWING);
    } else if (typeId == AccountTypesMap.getId(AccountTypes.RESTRICTED_SAVINGS)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.RESTRICTED_SAVINGS);
    } else {
      // If the account type is not valid, return null
      toReturn = null;
    }
    return toReturn;
  }

  /**
   * Get the name of the role from the given role ID.
   *
   * @param id is the role ID
   * @return the name of the role
   * @throws SQLException if there is a problem accessing the database
   */
  public static String getRole(int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    String role = "";
    try {
      role = DatabaseSelector.getRole(id, connection);
    } catch (SQLException e) {
      System.out.println("Something went wrong with the database in getRole");
    } finally{
      connection.close();
    }
    return role;
  }

  /**
   * Get the given user's password.
   *
   * @param userId is the user's ID
   * @return the password of the given user
   * @throws SQLException if there is a problem retrieving info from the database
   */
  public static String getPassword(int userId) throws SQLException {
    String hashPassword;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    hashPassword = DatabaseSelector.getPassword(userId, connection);
    connection.close();
    return hashPassword;
  }

  /**
   * Get the user details from the given user ID.
   *
   * @param userId is the user's ID
   * @return a user with the info of user ID's
   * @throws SQLException if there is a problem accessing the database
   */
  public static User getUserDetails(int userId) throws SQLException {
    User user = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      ResultSet results = DatabaseSelector.getUserDetails(userId, connection);
      String name;
      int age;
      String address;
      int roleId;
      while (results.next()) {
        //It may help you to figure out the user's type, and make an obj based on its
        name = results.getString("NAME");
        age = results.getInt("AGE");
        address = results.getString("ADDRESS");                    
        roleId = results.getInt("ROLEID");
        user = DatabaseSelectHelper.buildUser(userId, name, age, address, roleId);
      }
    } catch (SQLException e) {
      System.out.println("Something went wrong with database in gtUserDetails");
    } finally {
      connection.close();
    }
    return user;
  }

  /**
   * Get a list of all the account IDs associated to a user.
   *
   * @param userId is the user's id
   * @return a list of all the account ID's associated with user
   * @throws SQLException if there is a problem with the connection and database
   */
  public static List<Integer> getAccountIds(int userId) throws SQLException {
    // Create an empty list to store the account ids
    List<Integer> accountIds = new ArrayList<Integer>();
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountIds(userId, connection);
    while (results.next()) {
      // for each set of results, add the account id to the list of account ids
      accountIds.add(results.getInt("ACCOUNTID"));
    }
    connection.close();
    return accountIds;
  }

  /**
   * Get the account details of the given account.
   *
   * @param accountId is the account's ID
   * @return an account with the same info as given account
   * @throws SQLException if there is a problem with the database
   */
  public static Account getAccountDetails(int accountId) throws SQLException {
    // Create a default null account
    Account account = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
    // Create variables for the name, balance, and account type
    String name;
    String balance;
    int accountType;
    while (results.next()) {
      //It may help you to figure out the account type, and make an obj based on its
      name = results.getString("NAME");
      balance = results.getString("BALANCE");
      accountType = results.getInt("TYPE");
      account = DatabaseSelectHelper.buildAccount(accountId, name, balance, accountType);
    }
    connection.close();
    return account;
  }

  /**
   * Get the account's balance.
   *
   * @param accountId is the account's ID
   * @return the account's balance
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static BigDecimal getBalance(int accountId) throws SQLException {
    //TODO Implement this method as stated on the assignment sheet (Strawberry)
    //hint: The below code should help you out a little
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    BigDecimal balance = DatabaseSelector.getBalance(accountId, connection);
    connection.close();
    return balance;
  }

  /**
   * Get the given account type's interest rate.
   *
   * @param accountType is the account type ID
   * @return the interest rate associated with the given account type
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static BigDecimal getInterestRate(int accountType) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    BigDecimal interestRate = DatabaseSelector.getInterestRate(accountType, connection);
    connection.close();
    return interestRate;
  }

  /**
   * Get a list of all the account type IDs.
   *
   * @return a list of all the account type IDs
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static List<Integer> getAccountTypesIds() throws SQLException {
    //TODO Implement this method to get all account Type Id's stored in the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountTypesId(connection);
    List<Integer> ids = new ArrayList<>();
    while (results.next()) {
      ids.add(results.getInt("ID"));
    }
    connection.close();
    return ids;
  }

  /**
   * Get the account type name associated to given type ID.
   *
   * @param accountTypeId is the account type ID
   * @return the account type name
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static String getAccountTypeName(int accountTypeId) throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    String typeName = DatabaseSelector.getAccountTypeName(accountTypeId, connection);
    connection.close();
    return typeName;
  }

  /**
   * Get a list of all the role IDs.
   *
   * @return a list of all the role IDs
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static List<Integer> getRoles() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getRoles(connection);
    List<Integer> ids = new ArrayList<>();
    while (results.next()) {
      ids.add(results.getInt("ID"));
    }
    connection.close();
    return ids;
  }

  /**
   * Get the account type of the given account.
   *
   * @param accountId is the account's ID
   * @return the account type ID of the given account
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static int getAccountType(int accountId) throws SQLException {
    //TODO implement this method
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int accountType = DatabaseSelector.getAccountType(accountId, connection);
    connection.close();
    return accountType;
  }

  /**
   * Get the given user's role ID.
   *
   * @param userId is the user's ID
   * @return the user's role ID
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static int getUserRole(int userId) throws SQLException {
    //TODO implement this method
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int roleId = DatabaseSelector.getUserRole(userId, connection);
    connection.close();
    return roleId;
  }

  /**
   * Get a list of all Admins.
   * @return a list of all Admins
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static List<Admin> getAdmins() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Admin> admins = new ArrayList<>();
    User user;
    int userId;
    String name;
    int age;
    String address;
    int roleId;
    boolean noMoreUsers = false;
    int id = 1;
    while (!noMoreUsers) {
      ResultSet results = DatabaseSelector.getUserDetails(id, connection);
      if (!results.isBeforeFirst()) {
        noMoreUsers = true;
      } else if (results.getInt("ROLEID") == RolesMap.getId(Roles.ADMIN)) { // TODO:
        userId = results.getInt("ID");
        name = results.getString("NAME");
        age = results.getInt("AGE");
        address = results.getString("ADDRESS");
        roleId = results.getInt("ROLEID");
        user = DatabaseSelectHelper.buildUser(userId, name, age, address, roleId);
        admins.add((Admin) user);
      }
      id++;
    }
    connection.close();
    return admins;
  }

  /**
   * Get a list of all Customers.
   * @return a list of all Customers
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static List<Customer> getCustomers() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Customer> customers = new ArrayList<>();
    User user;
    int userId;
    String name;
    int age;
    String address;
    int roleId;
    boolean noMoreUsers = false;
    int id = 1;
    while (!noMoreUsers) {
      ResultSet results = DatabaseSelector.getUserDetails(id, connection);
      if (!results.isBeforeFirst()) {
        noMoreUsers = true;
      } else if (results.getInt("ROLEID") == RolesMap.getId(Roles.CUSTOMER)) { // TODO:
        userId = results.getInt("ID");
        name = results.getString("NAME");
        age = results.getInt("AGE");
        address = results.getString("ADDRESS");
        roleId = results.getInt("ROLEID");
        user = DatabaseSelectHelper.buildUser(userId, name, age, address, roleId);
        customers.add((Customer) user);
      }
      id++;
    }
    connection.close();
    return customers;
  }

  /**
   * Get a list of all Tellers.
   * @return a list of all Tellers
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public static List<Teller> getTellers() throws SQLException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Teller> tellers = new ArrayList<>();
    User user;
    int userId;
    String name;
    int age;
    String address;
    int roleId;
    boolean noMoreUsers = false;
    int id = 1;
    while (!noMoreUsers) {
      ResultSet results = DatabaseSelector.getUserDetails(id, connection);
      if (!results.isBeforeFirst()) {
        noMoreUsers = true;
      } else if (results.getInt("ROLEID") == RolesMap.getId(Roles.TELLER)) { // TODO:
        userId = results.getInt("ID");
        name = results.getString("NAME");
        age = results.getInt("AGE");
        address = results.getString("ADDRESS");
        roleId = results.getInt("ROLEID");
        user = DatabaseSelectHelper.buildUser(userId, name, age, address, roleId);
        tellers.add((Teller) user);
      }
      id++;
    }
    connection.close();
    return tellers;
  }

  /**
   * Returns a list of all the users in the database as User objects
   * @return a list of all users in database represented as User objects
   * @throws SQLException
   */
  public static List<User> getAllUsers() throws SQLException {
    List<User> allUsers = new ArrayList<>();
    allUsers.addAll(getAdmins());
    allUsers.addAll(getTellers());
    allUsers.addAll(getCustomers());
    return allUsers;
  }

  /**
   * Get the list of messages that the given user has.
   * 
   * @param userId is the ID of the user whose messages you want to see.
   * @param stealth is true if you don't want to update the viewed status, otherwise its false.
   * @return The List of all messages to the user.
   * @throws SQLException
   */
  public static List<String> getAllMessages(int userId, boolean stealth)  throws SQLException {
    ResultSet messageTable;
    String currentMessage;
    List<String> messageList = new ArrayList<String>();
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    messageTable =  DatabaseSelector.getAllMessages(userId, connection);
    while (messageTable.next()) {
      currentMessage = messageTable.getString("MESSAGE");
      if (!stealth) {
        int messageId = messageTable.getInt("ID");
        DatabaseUpdateHelper.updateUserMessageState(messageId);
      }
      messageList.add(currentMessage);
    }
    connection.close();
    return messageList;
  }

  
  /**
   * Gets the ID of every message sent to the user.
   * 
   * @param userId is the ID of the user whose messages you want to get.
   * @return a list of message IDs
   * @throws SQLException when there is a problem with the connection to the database.
   */
  public static List<Integer> getMessageIds(int userId) throws SQLException {
    ResultSet messageTable;
    int currentMessageId;
    List<Integer> idList = new ArrayList<Integer>();
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    messageTable = DatabaseSelectHelper.getAllMessages(userId, connection);
    while (messageTable.next()) {
      currentMessageId = messageTable.getInt("ID");
      idList.add(currentMessageId);
    }
    connection.close();
    return idList;
  }

  public static String getMessageViewState(int messageId) throws SQLException{
    String message;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    message = DatabaseSelector.getMessageViewedState(messageId, connection);
    connection.close();
    return message;
  }

  /**
   * Gets a specific message.
   * 
   * @param messageId is the ID of the message which you want to get.
   * @param stealth is true if you don't want to the viewed status to change, otherwise false.
   * @return The desired message as a string.
   * @throws SQLException
   */
  public static String getSpecificMessage(int messageId, boolean stealth) throws SQLException{
    String message;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    message = DatabaseSelector.getSpecificMessage(messageId, connection);
    connection.close();
    if (!stealth) {
      DatabaseUpdateHelper.updateUserMessageState(messageId);
    }
    return message;
  }

  /**
   * Gets a list of the IDs of all user who use the account with the given account ID.
   * 
   * @param accountId is the ID of the account.
   * @return A list of IDs, where the IDs belong to customers who use the given account.
   * @throws SQLException is thrown when there is an issue connecting to the database.
   */
  public static List<Integer> getUserIds(int accountId) throws SQLException {
    List<Integer> userIds = new ArrayList<Integer>();
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserIds(accountId, connection);
    while (results.next()) {
      userIds.add(results.getInt("USERID"));
    }
    connection.close();
    return userIds;
  }
}
