package com.bank.bank;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bank.account.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasemap.AccountTypesMap;
import com.bank.generics.AccountTypes;
import com.bank.user.Customer;
import com.bank.user.User;

/**
 * Represents the ATM that the customers can interact with.
 * @author Maxx Karpowicz
 *
 */

public class ATM extends BankMachine implements ATMInterface {

  private Customer currentCustomer;
  private boolean authenticated;

  /**
   * Checks if the given user is a customer.
   * @param user is a generic user.
   * @return true if the given user is of the type customer, otherwise return false.
   */
  private boolean isCustomer(User user) {
    boolean  thisIsCustomer;
    if (user instanceof Customer) {
      thisIsCustomer = true;
    }
    else {
      thisIsCustomer = false;
    }
    return thisIsCustomer;
    
  }

  /**
   * Constructs an ATM
   * @param customerId is the id for the customer who would use the ATM.
   * @param password is the password that the customer would use to get into their account.
   * @throws SQLException if there is a problem with the query
   */
  public ATM(int customerId, String password) throws SQLException {
    User potentialCustomer = DatabaseSelectHelper.getUserDetails(customerId);
    if (isCustomer(potentialCustomer)) {
      this.currentCustomer = (Customer) potentialCustomer;
      super.setCurrentCustomer(this.currentCustomer);
      //This will check the password.
      //If the password is correct then the currentCustomer,
      // and the ATM will be authenticated.
      this.authenticated = this.currentCustomer.authenticate(password);
    } else if (customerId != -1){
      System.out.println("This is not a valid customer account.");
    }
  }

  /**
   * Constructs an ATM but does not authenticate the customer who would use the ATM.
   * @param customerId is the ID of the customer.
   * @throws SQLException if there is a problem with the query
   */
  public ATM(int customerId) throws SQLException {
    User potentialCustomer = DatabaseSelectHelper.getUserDetails(customerId);
    if (isCustomer(potentialCustomer)) {
     this.currentCustomer = (Customer) potentialCustomer;
     super.setCurrentCustomer(this.currentCustomer);
    } else {
      System.out.println("This is not a valid customer account.");
    }
  }

  /**
   * Authenticates a customer who would use the ATM.
   * @param userId the id of the user.
   * @param password the password of the user.
   * @return true if the account was authenticated, otherwise return false.
   * @throws SQLException if there is a problem with the query
   */
  public boolean authenticate(int userId, String password) throws SQLException {
    User userToAuthenticate = DatabaseSelectHelper.getUserDetails(userId);
    boolean authenticated = userToAuthenticate.authenticate(password);
    this.authenticated = authenticated;
    return authenticated;
  }

  /**
   * Gets the list of accounts that are tied to the customer.
   * @return the list of accounts of the customer.
   */
  @Override
  public List<Account> listAccounts() {
    if(this.authenticated) {
      return super.listAccounts();
    } else {
      System.out.println("You do not have access to this information.");
      return null;
    }
  }

  /**
   * Inserts the desired amount into the account.
   * @param amount is the amount that you wish to deposit.
   * @param accountId is the id for the account that you want to deposit into.
   * @return true if the deposit was successful, otherwise return false.
   * @throws SQLException if there is a problem with the query
   */
  @Override
  public boolean makeDeposit(BigDecimal amount, int  accountId) throws SQLException {
    if(this.authenticated) {
      return super.makeDeposit(amount, accountId);
    } else {
      System.out.println("You do not have permission to complete this action.");
      return false;
    }
  }

  /**
   * Makes a withdrawal from the desired account of the user.
   * @param amount is the amount that you wish to withdraw.
   * @param accountId is the ID of the account you wish to withdraw from.
   * @return true if the funds where withdrawn successfully, otherwise return false.
   * @throws SQLException if there is a problem with the query
   */
  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws SQLException {
    if(this.authenticated) {
      // Check if the account type is RestrictedSavings
      int typeId = DatabaseSelectHelper.getAccountType(accountId);
      int restrictedId = AccountTypesMap.getId(AccountTypes.RESTRICTED_SAVINGS);
      if (typeId == restrictedId) {
    	System.out.println("Please use the TellerTerminal to withdraw from this account.");
        return false;
      } else {
        return super.makeWithdrawal(amount, accountId);
      }
    } else {
      System.out.println("You do not have permission to complete this action.");
      return false;
    }
  }

  /**
   * Shows the current balance in the desired account.
   * @param accountId is the ID of the account that the user is tied to.
   * @return the balance if successful, otherwise return null.
   * @throws SQLException if there is a problem with the query
   */
  @Override
  public BigDecimal checkBalance(int accountId) throws SQLException {
    if(this.authenticated) {
      return super.checkBalance(accountId);
    } else {
      System.out.println("You do not have permission to complete this action.");
      return null;
    }
  }
  
  public Customer getCurrentCustomer() {
    return this.currentCustomer;
  }
  /**
   * Sets who the current customer of the ATM is.
   * @param customer is a customer type user.
   */
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
  }

  /**
   * Lists all the message IDs tied to the customer.
   */
  public String listMessageIds() {
    return currentCustomer.PrintMessageIds();
  }

  /**
   * Prints the desired message.
   * @param messageId is the ID of the desired message.
   */
  public String printMessage(int messageId) {
    return currentCustomer.PrintMessage(messageId, false);
  }
}
