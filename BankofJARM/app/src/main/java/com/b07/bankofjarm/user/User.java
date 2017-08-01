package com.b07.bankofjarm.user;

import android.app.Application;
import com.b07.bankofjarm.databasehelper.DatabaseInsertHelper;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.security.PasswordHelpers;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public abstract class User implements Serializable {
  private static final long serialVersionUID = 8924708152697574031L;

  private transient int id;
  private String name;
  private int age;
  private String address;
  private Roles role;
  private transient boolean authenticated;

  protected User(int id, String name, int age, String address, boolean authenticated, Roles role) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.address = address;
    this.authenticated = authenticated;
    this.role = role;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return this.age;
  }

  public void setAge(int age) {
    this.age = age;
  }
  
  public String getAddress() {
    return this.address;
  }

  public int getRoleId() {
    RolesMap map = RolesMap.getInstance(null);
    return map.getId(this.role);
  }
  
  public String getRoleName() throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    String role = instance.getRole(this.id);
    instance.close();
    return role;
  }

  public Roles getRole() {
    return this.role;
  }
  
  public void setAddress(String address) {
	this.address = address;
  }

  /**
   * Sets the authentication status of the user to true if the given plain-text password is
   * correct.
   * @param password, the user's plain-text password
   * @return true if the user is authenticated, false otherwise
   * @throws SQLException if there is a problem retrieving the password in the database
   */
  public final boolean authenticate(String password) throws SQLException {
    // Gets unhashed password of the user from the database, sets authenticated to true if equal

    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    String unhashedPassword = instance.getPassword(this.getId());
    instance.close();
    this.authenticated = PasswordHelpers.comparePassword(unhashedPassword, password);
    return this.authenticated;
  }

  /**
   * Prints the the IDs of the messages sent to the user.
   */
  public String PrintMessageIds() {
    List<Integer> messageIds;
    String message = "";
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    try {
      messageIds = instance.getMessageIds(this.id);
      for (int id : messageIds) {
        message = message + "Message: " + id + " Status: ";
        String status = instance.getMessageViewState(id);
        if (status.equals("0")) {
          message = message + "Un-read \n";
        } else if (status.equals("1")) {
          message = message + "Read \n";
        }
      }
    } catch (SQLException e) {
        System.out.println("There was a problem connecting to the database.");
        e.printStackTrace();
    } finally {
      instance.close();
    }
    return message;
  }

  /**
   * Prints the desired message that has been sent to the current user.
   * @param messageId is the ID of the message you wish to view.
   * @param mode is false if you wish to change the viewed status of the message, otherwise false.
   */
  public String PrintMessage(int messageId, boolean mode) {
    List<Integer> userMessageIds;
    String message = "";
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    try {
      userMessageIds = instance.getMessageIds(this.id);
      if (userMessageIds.contains(messageId)) {
        message = instance.getSpecificMessage(messageId, mode);
      } else {
        System.out.println("This is not a message that has been sent to you.");
      }
    } catch (SQLException e) {
        System.out.println("There was a problem connecting to the database.");
        e.printStackTrace();
    } finally {
      instance.close();
    }
    return message;
  }

  /**
   * Sends a message to the desired user.
   * @param userId is the ID of the user whom you wish to message.
   * @param message is the message you want to send.
   */
  public void sendMessage(int userId, String message) {
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    try {
      instance.insertNewMessage(userId, message);
    } finally {
      instance.close();
    }
  }
}
