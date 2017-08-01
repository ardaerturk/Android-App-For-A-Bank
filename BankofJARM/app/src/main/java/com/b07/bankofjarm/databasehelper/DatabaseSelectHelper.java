package com.b07.bankofjarm.databasehelper;

import android.content.Context;
import android.database.Cursor;
import android.widget.EditText;
import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.database.DatabaseDriverA;
/*
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
*/
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.user.Admin;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.Teller;
import com.b07.bankofjarm.user.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSelectHelper extends DatabaseDriverA {
  private static DatabaseSelectHelper singleton = null;
  private DatabaseSelectHelper(Context context) {
    super(context);
  }

  public static DatabaseSelectHelper getInstance(Context context) {
    if (singleton == null) {
      singleton = new DatabaseSelectHelper(context);
    }
    return singleton;
  }


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
  private static User buildUser(int userId, String name, int age, String address, int roleId) {
    // Check which type of user we need to build
    User toReturn;
    RolesMap map = RolesMap.getInstance(null);

    if (roleId == map.getId(Roles.ADMIN)) {
      toReturn = new Admin(userId, name, age, address);
    } else if (roleId == map.getId(Roles.CUSTOMER)) {
      toReturn = new Customer(userId, name, age, address);
    } else if (roleId == map.getId(Roles.TELLER)) {
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
   */
  private Account buildAccount(int accountId, String name, String balance, int typeId)
      throws SQLException {
    // Check which type of account we need to build
    Account toReturn;
    // Convert the string balance to BigDecimal
    BigDecimal bigDecimalBalance = new BigDecimal(balance);
    AccountTypesMap map = AccountTypesMap.getInstance(null);

    if (typeId == map.getId(AccountTypes.CHEQUING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.CHEQUING);
    } else if (typeId == map.getId(AccountTypes.SAVING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.SAVING);
    } else if (typeId == map.getId(AccountTypes.TFSA)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.TFSA);
    } else if (typeId == map.getId(AccountTypes.BALANCE_OWING)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.BALANCE_OWING);
    } else if (typeId == map.getId(AccountTypes.RESTRICTED_SAVINGS)) {
      toReturn = new Account(accountId, name, bigDecimalBalance, AccountTypes.RESTRICTED_SAVINGS);
    }
    else {
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
   */
  public String getRole(int id) {
    return super.getRole(id);
  }

  /**
   * Get the given user's password.
   *
   * @param userId is the user's ID
   * @return the hashed password of the given user
   */
  public String getPassword(int userId) {
    return super.getPassword(userId);
  }

  /**
   * Get the user details from the given user ID.
   *
   * @param userId is the user's ID
   * @return a user with the info of user ID's
   */
  public User getUserObject(int userId) {
    try {
      Cursor cursor = super.getUserDetails(userId);

      cursor.moveToFirst();
      int id;
      String name;
      int age;
      String address;
      int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));

      id = cursor.getInt(cursor.getColumnIndex("ID"));
      name = cursor.getString(cursor.getColumnIndex("NAME"));
      age = cursor.getInt(cursor.getColumnIndex("AGE"));
      address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
      User user = buildUser(id, name, age, address, roleId);
      cursor.close();
      return user;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get a list of all the account IDs associated to a user.
   *
   * @param userId is the user's id
   * @return a list of all the account ID's associated with user
   * @throws SQLException if there is a problem with the connection and database
   */
  public List<Integer> getAccountIdsList(int userId) {
    Cursor cursor = super.getAccountIds(userId);
    List<Integer> list = new ArrayList<>();

    cursor.moveToFirst();
    while (cursor.moveToNext()) {
      list.add(cursor.getInt(cursor.getColumnIndex("ACCOUNTID")));
    }

    cursor.close();
    return list;
  }

  /**
   * Gets the IDs of all user linked with an account.
   * @param accountId is the ID of the account of which you want to check the users.
   * @return A list of the IDs of users.
   */
  public List<Integer> getUserIds(int accountId) {
    List<Integer> userIds = new ArrayList<Integer>();
    Cursor cursor = super.getUsersDetails();

    while (cursor.moveToNext()) {
      int userId = cursor.getInt(cursor.getColumnIndex("ID"));
      // TODO: two cursors may cause a problem
      if (this.getAccountIdsList(userId).contains(accountId)) {
        userIds.add(userId);
      }
    }
    return userIds;
  }

  /**
   * Get the account details of the given account.
   *
   * @param accountId is the account's ID
   * @return an account with the same info as given account
   * @throws SQLException if there is a problem with the database
   */
  public Account getAccountObject(int accountId) throws SQLException {
    Cursor cursor = super.getAccountDetails(accountId);
    String name;
    String balance;
    int accountType;
    Account account = null;

    while (cursor.moveToNext()) {
      name = cursor.getString(cursor.getColumnIndex("NAME"));
      balance = cursor.getString(cursor.getColumnIndex("BALANCE"));
      accountType = cursor.getInt(cursor.getColumnIndex("TYPE"));
      account = buildAccount(accountId, name, balance, accountType);
    }

    cursor.close();
    return account;
  }
//
  /**
   * Get the account's balance.
   *
   * @param accountId is the account's ID
   * @return the account's balance
   */
  public BigDecimal getBalance(int accountId) {
    return super.getBalance(accountId);
  }

  /**
   * Get the given account type's interest rate.
   *
   * @param accountType is the account type ID
   * @return the interest rate associated with the given account type
   */
  public BigDecimal getInterestRate(int accountType) {
    return super.getInterestRate(accountType);
  }

  /**
   * Get a list of all the account type IDs.
   *
   * @return a list of all the account type IDs
   */
  public List<Integer> getAccountTypesIdList() {
    Cursor cursor = super.getAccountTypesId();
    List<Integer> types = new ArrayList<>();

    while (cursor.moveToNext()) {
      types.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }

    return types;
  }

  /**
   * Get the account type name associated to given type ID.
   *
   * @param accountTypeId is the account type ID
   * @return the account type name
   */
  public String getAccountTypeName(int accountTypeId) {
    return super.getAccountTypeName(accountTypeId);
  }

  /**
   * Get a list of all the role IDs.
   *
   * @return a list of all the role IDs
   */
  public List<Integer> getRolesList() {
    Cursor cursor = super.getRoles();
    List<Integer> roles = new ArrayList<>();
    while (cursor.moveToNext()) {
      roles.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    cursor.close();
    return roles;
  }

  /**
   * Get the account type of the given account.
   *
   * @param accountId is the account's ID
   * @return the account type ID of the given account
   */
  public int getAccountType(int accountId) {
    try {
      return super.getAccountType(accountId);
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * Get the given user's role ID.
   *
   * @param userId is the user's ID
   * @return the user's role ID
   */
  public int getUserRole(int userId) {
    return super.getUserRole(userId);
  }


  /**
   * Get a list of all Admins.
   *
   * @return a list of all Admins
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public List<Admin> getAdmins() {
    Cursor cursor = super.getUsersDetails();
    List<Admin> admins = new ArrayList<>();

    RolesMap map = RolesMap.getInstance(null);
    while (cursor.moveToNext()) {
      int id;
      String name;
      int age;
      String address;
      int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));

      if (roleId == map.getId(Roles.ADMIN)) {
        id = cursor.getInt(cursor.getColumnIndex("ID"));
        name = cursor.getString(cursor.getColumnIndex("NAME"));
        age = cursor.getInt(cursor.getColumnIndex("AGE"));
        address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        admins.add((Admin) buildUser(id, name, age, address, roleId));
      }
    }

    cursor.close();
    return admins;
  }

  /**
   * Get a list of all Customers.
   *
   * @return a list of all Customers
   */
  public List<Customer> getCustomers() {
    Cursor cursor = super.getUsersDetails();
    List<Customer> customers = new ArrayList<>();

    RolesMap map = RolesMap.getInstance(null);
    while (cursor.moveToNext()) {
      int id;
      String name;
      int age;
      String address;
      int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));

      if (roleId == map.getId(Roles.CUSTOMER)) {
        id = cursor.getInt(cursor.getColumnIndex("ID"));
        name = cursor.getString(cursor.getColumnIndex("NAME"));
        age = cursor.getInt(cursor.getColumnIndex("AGE"));
        address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        customers.add((Customer) buildUser(id, name, age, address, roleId));
      }
    }

    cursor.close();
    return customers;
  }

  /**
   * Get a list of all Users.
   *
   * @return a list of all Tellers
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public List<User> getAllUsers() throws SQLException {
    Cursor cursor = super.getUsersDetails();
    List<User> users = new ArrayList<>();

    while (cursor.moveToNext()) {
      int id;
      String name;
      int age;
      String address;
      int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));

      id = cursor.getInt(cursor.getColumnIndex("ID"));
      name = cursor.getString(cursor.getColumnIndex("NAME"));
      age = cursor.getInt(cursor.getColumnIndex("AGE"));
      address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
      users.add(buildUser(id, name, age, address, roleId));
    }

    cursor.close();
    return users;
  }

  /**
   * Get a list of all Tellers.
   *
   * @return a list of all Tellers
   * @throws SQLException if there is a problem accessing the connection and database
   */
  public List<Teller> getTellers() throws SQLException {
    Cursor cursor = super.getUsersDetails();
    List<Teller> tellers = new ArrayList<>();

    RolesMap map = RolesMap.getInstance(null);
    while (cursor.moveToNext()) {
      int id;
      String name;
      int age;
      String address;
      int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));

      if (roleId == map.getId(Roles.TELLER)) {
        id = cursor.getInt(cursor.getColumnIndex("ID"));
        name = cursor.getString(cursor.getColumnIndex("NAME"));
        age = cursor.getInt(cursor.getColumnIndex("AGE"));
        address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        tellers.add((Teller) buildUser(id, name, age, address, roleId));
      }
    }

    cursor.close();
    return tellers;
  }

  /**
   * Get the list of messages that the given user has.
   *
   * @param userId is the ID of the user whose messages you want to see.
   * @param stealth is true if you don't want to update the viewed status, otherwise its false.
   * @return The List of all messages to the user.
   * @throws SQLException
   */
  public List<String> getAllMessages(int userId, boolean stealth)  {
    Cursor cursor = super.getAllMessages(userId);
    List<String> messages = new ArrayList<>();
    while (cursor.moveToNext()) {
      String message = cursor.getString(cursor.getColumnIndex("MESSAGE"));
      if (!stealth) {
        DatabaseUpdateHelper dab = DatabaseUpdateHelper.getInstance(null);
        dab.updateUserMessageState(cursor.getInt(cursor.getColumnIndex("ID")));
      }
      messages.add(message);
    }
    return messages;
  }

  /**
   * Gets the ID of every message sent to the user.
   *
   * @param userId is the ID of the user whose messages you want to get.
   * @return a list of message IDs
   */
  public List<Integer> getMessageIds(int userId) throws SQLException {
    Cursor cursor = super.getAllMessages(userId);
    List<Integer> ids = new ArrayList<>();

    while (cursor.moveToNext()) {
      ids.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }

    return ids;
  }

  /**
   * Gets a specific message.
   *
   * @param messageId is the ID of the message which you want to get.
   * @param stealth is true if you don't want to the viewed status to change, otherwise false.
   * @return The desired message as a string.
   */
  public String getSpecificMessage(int messageId, boolean stealth) throws SQLException{
    String message = super.getSpecificMessage(messageId);

    if (!stealth) {
      DatabaseUpdateHelper instance = DatabaseUpdateHelper.getInstance(null);
      instance.updateUserMessageState(messageId);
      instance.close();
    }

    return message;
  }

  public String getMessageViewState(int messageId) {
    // TODO: write javadoc and write message viewed state
    try {
      return super.getMessageViewState(messageId);
    } catch (Exception e) {
      return "0";
    }
  }

}
