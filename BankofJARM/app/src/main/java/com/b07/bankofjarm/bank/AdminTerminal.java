package com.b07.bankofjarm.bank;


import com.b07.bankofjarm.databasehelper.DatabaseInsertHelper;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.security.PasswordHelpers;
import com.b07.bankofjarm.user.Admin;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.Teller;
import com.b07.bankofjarm.user.User;
import java.sql.SQLException;
import java.util.List;


/**
 * @author Arda Erturk.
 */
public class AdminTerminal {

  public static boolean login(int userId, String password) {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    boolean correctRole =
        instance.getUserRole(userId) == RolesMap.getInstance(null).getId(Roles.ADMIN);
    boolean correctPassword = PasswordHelpers
        .comparePassword(instance.getPassword(userId), password);
    instance.close();
    return correctPassword && correctRole;
  }


  /**
   * Creates a new Admin in the database.
   *
   * @param name the name of the Admin.
   * @param age is the Admin's age
   * @param address is the address of the Admin.
   * @param password is the password of the Admin.
   * @return true if the user becomes an Admin, otherwise return false.
   * @throws SQLException if there is a problem with the database
   */
  public static int makeNewAdmin(String name, int age, String address, String password)
      throws SQLException {
    // create an admin by using the given information
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    RolesMap map = RolesMap.getInstance(null);
    int id = instance.insertUser(name, age, address, map.getId(Roles.ADMIN), password);
    instance.close();
    return id;
  }

  /**
   * creates a new Teller.
   *
   * @param name is the name of the Teller.
   * @param age is the Teller's age.
   * @param address is the adress of the Teller
   * @param password is the password of the Teller.
   * @return true if new Teller is created, false otherwise.
   * @throws SQLException if there is a problem with the database
   */
  public static int makeNewTeller(String name, int age, String address, String password)
      throws SQLException {
    // create a Teller by using the given information
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    RolesMap map = RolesMap.getInstance(null);
    int id = instance.insertUser(name, age, address, map.getId(Roles.TELLER), password);
    instance.close();
    return id;
  }

  /**
   * Get all the current admins on the database.
   *
   * @return the admins on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Admin> getAdmins() throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    List<Admin> admins = instance.getAdmins();
    instance.close();
    return admins;
  }

  /**
   * Get all the current tellers on the database.
   *
   * @return the tellers on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Teller> getTellers() throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    List<Teller> tellers = instance.getTellers();
    instance.close();
    return tellers;
  }

  /**
   * Get all the current tellers on the database.
   *
   * @return the tellers on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Customer> getCustomers() throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    List<Customer> customers = instance.getCustomers();
    instance.close();
    return customers;
  }

  /**
   * Checks the messages of an administrator.
   *
   * @param currentAdmin is the current administrator logged in.
   * @param messageId is the ID of the administrator's message you wish to view.
   */
  public static String viewMyMessages(User currentAdmin, int messageId) {
    return currentAdmin.PrintMessage(messageId, false);
  }

  /**
   * Sends a message to the desired user.
   *
   * @param currentAdmin the administrator currently logged in.
   * @param recipientId the ID of the user you wish to message.
   * @param message is the message you want to send.
   */
  public static void sendMessage(User currentAdmin, int recipientId, String message) {
    currentAdmin.sendMessage(recipientId, message);
  }

  /**
   * Allows the user to view anyone else's messages without changing the viewed status.
   *
   * @param userToView the user who's messages you wish to view.
   * @param messageId the ID of the message you wish to view.
   */
  public static String viewOtherMessages(User userToView, int messageId) {
    return userToView.PrintMessage(messageId, true);
  }
}