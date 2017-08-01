package com.b07.bankofjarm.bank;

import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.databasehelper.DatabaseInsertHelper;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.Teller;
import com.b07.bankofjarm.user.User;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents the object that the tellers will be interacting with.
 *
 * @author Maxx Karpowicz
 */

public class TellerTerminal extends BankMachine implements TellerTerminalInterface {

  private Teller currentUser;
  private boolean currentUserAuthenticated;
  private Customer currentCustomer = null;
  private boolean currentCustomerAuthenticated;

  /**
   * Checks if the given user is of the teller type.
   *
   * @param user is any user or child of user.
   * @return true if the given user is a teller, otherwise it returns false.
   */
  private boolean isTeller(User user) {
    boolean isTeller;
    if (user instanceof Teller) {
      isTeller = true;
    } else {
      isTeller = false;
    }
    return isTeller;
  }

  public int getCurrentCustomerId() {
    return currentCustomer.getId();
  }

  /**
   * Get the current customer's id
   *
   * @return the id of the customer.
   */
  public int getId() {
    return this.currentCustomer.getId();
  }

  /**
   * Creates the teller terminal.
   *
   * @param tellerId the ID of a teller account in the database.
   * @param password the password associated with the given account.
   */
  public TellerTerminal(int tellerId, String password) throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    User potentialTeller = instance.getUserObject(tellerId);
    instance.close();
    if (isTeller(potentialTeller)) {
      this.currentUser = (Teller) potentialTeller;
      this.currentUserAuthenticated = this.currentUser.authenticate(password);
      if (this.currentUserAuthenticated) {
        System.out.println("Welcome!");
      } else {
        System.out.println("The given  password is Incorect, Teller authentication failed.");
      }
    } else {
      System.out.println("This is not a valid teller ID.");
    }
  }

  /**
   * Creates a new account in the database.
   *
   * @param name the name of the new account.
   * @param balance the initial balance of the new account.
   * @param type the account type of the new account.
   * @return true if the new account was created, otherwise return false.
   * @throws SQLException if there is a problem with the query
   */
  public boolean makeNewAccount(String name, BigDecimal balance, int type) throws SQLException {
    if (this.currentCustomer == null) {
      System.out.println("Please set a current customer first!");
      return false;
    }
    DatabaseInsertHelper insertInstance = DatabaseInsertHelper.getInstance(null);
    DatabaseSelectHelper selectInstance = DatabaseSelectHelper.getInstance(null);
    int newId = (insertInstance.insertNewAccount(name, balance, type));
    int success = insertInstance.insertNewUserAccount(this.currentCustomer.getId(), newId);
    Account newAccount = selectInstance.getAccountObject(newId);
    selectInstance.close();
    insertInstance.close();

    if (newId == -1 || success == -1 || newAccount == null) {
      return false;
    } else {
      this.currentCustomer.addAccount(newAccount);
      return true;
    }
  }

  /**
   * Sets who the current customer of the teller is.
   *
   * @param customer is a customer type user.
   */
  @Override
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
    super.setCurrentCustomer(customer);
  }

  /**
   * Checks if the log in password is correct for the current customer.
   *
   * @param password the customer's user password.
   * @throws SQLException if there is a problem with the query
   * @returns true if the Customer was authenticated, false otherwise
   */
  public boolean authenticateCurrentCustomer(String password) throws SQLException {
    if (this.currentCustomer == null) {
      System.out.println("There is no current customer to authenticate.");
      return false;
    } else {
      this.currentCustomerAuthenticated = this.currentCustomer.authenticate(password);
      if (this.currentCustomerAuthenticated) {
        System.out.println(currentCustomer.getName() + (", ") + currentCustomer.getAddress());
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Adds a new user into the database.
   *
   * @param name the name of the new user.
   * @param age the age of the new user.
   * @param address the address of the new user.
   * @param password the password that will be linked to the new user.
   * @throws SQLException if there is a problem with the query
   */
  public void makeNewUser(String name, int age, String address, String password)
      throws SQLException {
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    RolesMap map = RolesMap.getInstance(null);
    int worked = instance.insertUser(name, age, address, map.getId(Roles.CUSTOMER), password);
    instance.close();
    if (worked == -1) {
      System.out.println("Something went wrong.");
    } else {
      System.out.println("New customer added with id: " + worked);
    }
  }

  /**
   * Adds the interest into the specified account of the current customer.
   *
   * @param accountId is the ID number for the account that the interest should be added into.
   * @throws SQLException if there is a problem with the query
   */
  public void giveInterest(int accountId) throws SQLException {
    if (this.currentUserAuthenticated) {
      if (this.currentCustomerAuthenticated) {
        DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
        if (instance.getAccountIdsList(this.currentCustomer.getId()).contains(accountId)) {
          List<Account> accounts = this.currentCustomer.getAccounts();
          Account cAccount = null;
          for (Account account : accounts) {
            if (account != null) {
              if (account.getId() == accountId) {
                cAccount = account;
              }
            }
          }
          instance.close();

          if (cAccount == null) {
            return;
          }

          cAccount.findAndSetInterestRate();
          cAccount.addInterest();
        } else {
          System.out.println("This account does not belong to this customer.");
        }
      } else {
        System.out.println("Customer not authenticated.");
      }
    } else {
      System.out.println("Teller not authenticated.");
    }
  }

  /**
   * Links a user and account in the database.
   *
   * @param userId is the ID of the user to which the account will belong.
   * @param accountId is the ID of the account to which you wish to give the user.
   */
  public void linkUserAccount(int userId, int accountId) {
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    instance.insertNewUserAccount(userId, accountId);
  }

  /**
   * Gives interest to an account in the database
   *
   * @param account is the ID for an account in the database.
   */
  public void giveInterestToAccount(int accountID) throws SQLException {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    Account account = instance.getAccountObject(accountID);
    account.findAndSetInterestRate();
    account.addInterest();
    List<Integer> userIds = instance.getUserIds(accountID);
    instance.close();
    for (Integer id : userIds) {
      currentUser.sendMessage(id.intValue(), "Interest was added to your account with ID: "
          + accountID);
    }
  }

  public void messageUser(int userId, String message) {
    DatabaseInsertHelper instance = DatabaseInsertHelper.getInstance(null);
    instance.insertNewMessage(userId, message);
  }

  /**
   * Changes the current user to none and log out of their account.
   */
  public void deAuthenticateCustomer() {
    this.currentCustomerAuthenticated = false;
    this.currentCustomer = null;
  }

  @Override
  public boolean makeDeposit(BigDecimal amount, int accountId) throws SQLException {
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      return super.makeDeposit(amount, accountId);
    } else {
      System.out.println("You do not have permission to complete this action.");
      return false;
    }
  }

  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws SQLException {
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      return super.makeWithdrawal(amount, accountId);
    } else {
      System.out.println("You do not have permission to complete this action.");
      return false;
    }
  }

  @Override
  public BigDecimal checkBalance(int accountId) throws SQLException {
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      return super.checkBalance(accountId);
    } else {
      System.out.println("You do not have permission to complete this action.");
      return null;
    }
  }

  @Override
  public List<Account> listAccounts() {
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      return super.listAccounts();
    } else {
      System.out.println("You do not have permission to complete this action.");
      return null;
    }
  }

  public boolean isCurrentUserAuthenticated() {
    // TODO Auto-generated method stub
    return this.currentUserAuthenticated;
  }

  /**
   * Prints the desired message if it belongs to the user.
   *
   * @param messageId is the ID of the message you wish to print.
   */
  public String viewSpecificMessage(int messageId) {
    String message = currentUser.PrintMessage(messageId, false);
    return message;
  }

  /**
   * Lists the IDs of each of the current tellers messages.
   */
  public String listTellerMessageIds() {
    return currentUser.PrintMessageIds();
  }

  /**
   * Displays the desired message of the current customer.
   *
   * @param messageId is the ID of the message that you which to view that belongs to the customer.
   */
  public String viewCustomerMessage(int messageId) {
    return currentCustomer.PrintMessage(messageId, false);
  }

  /**
   * Prints the IDs of the current users messages.
   */
  public String listCustomerMessageIds() {
    return currentCustomer.PrintMessageIds();
  }

  public void sendMessageToCustomer(int userId, String message) {
    User potentialCustomer;
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    potentialCustomer = instance.getUserObject(userId);
    instance.close();
    RolesMap map = RolesMap.getInstance(null);
    if (potentialCustomer.getRoleId() == map.getId(Roles.CUSTOMER)) {
      currentUser.sendMessage(userId, message);
    } else {
      System.out.println("That was not a customer's ID.");
    }
  }


  public Teller getCurrentUser() {
    return this.currentUser;
  }
}
