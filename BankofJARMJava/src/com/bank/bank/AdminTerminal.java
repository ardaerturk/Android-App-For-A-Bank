package com.bank.bank;

import com.bank.databasemap.RolesMap;
import com.bank.generics.Roles;
import com.bank.user.Admin;
import com.bank.user.Customer;
import com.bank.user.Teller;
import com.bank.user.User;

import java.sql.SQLException;

import com.bank.databasehelper.*;
import java.util.List;

/**
 * 
 * @author Arda Erturk.
 *
 */
public class AdminTerminal {

  /**
   * Creates a new Admin in the database.
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
    int id =
        DatabaseInsertHelper.insertNewUser(
            name, age, address, RolesMap.getId(Roles.ADMIN), password);
    return id;
  }

  /**
   * creates a new Teller.
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
    int id = DatabaseInsertHelper.insertNewUser(name, age, address, RolesMap.getId(Roles.TELLER), password);
      return id;
  }

  /**
   * Get all the current admins on the database.
   * @return the admins on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Admin> getAdmins() throws SQLException {
    List<Admin> admins = DatabaseSelectHelper.getAdmins();
    return admins;  
  }

  /**
   * Get all the current tellers on the database.
   * @return the tellers on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Teller> getTellers() throws SQLException {
    List<Teller> tellers = DatabaseSelectHelper.getTellers();
    return tellers;  
  }

  /**
   * Get all the current tellers on the database.
   * @return the tellers on the database
   * @throws SQLException if there is a problem with connecting to the database
   */
  public static List<Customer> getCustomers() throws SQLException {
    List<Customer> customers = DatabaseSelectHelper.getCustomers();
    return customers;  
  }

  /**
   * Checks the messages of an administrator.
   * @param currentAdmin is the current administrator logged in.
   * @param messageId is the ID of the administrator's message you wish to view.
   */
  public static String viewMyMessages(User currentAdmin, int messageId) {
    return currentAdmin.PrintMessage(messageId, false);
  }

  /**
   * Sends a message to the desired user.
   * @param currentAdmin the administrator currently logged in.
   * @param recipientId the ID of the user you wish to message.
   * @param message is the message you want to send.
   */
  public static void sendMessage(User currentAdmin, int recipientId, String message) {
    currentAdmin.sendMessage(recipientId, message);
  }

  /**
   * Allows the user to view anyone else's messages without changing the viewed status.
   * @param userToView the user who's messages you wish to view.
   * @param messageId the ID of the message you wish to view.
   */
  public static String viewOtherMessages(User userToView, int messageId) {
    return userToView.PrintMessage(messageId, true);
  }
}
