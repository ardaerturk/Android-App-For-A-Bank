package com.b07.bankofjarm.user;


import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jr on 21/06/17.
 */
public class Customer extends User {

  private static final long serialVersionUID = -3968022455871168108L;
  private List<Account> accounts;

  /**
   * Initializes a customer, without authentication.
   * @param id The id of the Customer
   * @param name The full name of the Customer
   * @param age The age of the customer in years
   * @param address The address of the customer
   */
  public Customer(int id, String name, int age, String address) {
    this(id, name, age, address, false);
  }

  /**
   * Initializes a customer.
   * @param id The id of the Customer
   * @param name The full name of the Customer
   * @param age The age of the customer in years
   * @param address The address of the customer
   */
  public Customer(int id, String name, int age, String address, boolean authenticated) {
    super(id, name, age, address, authenticated, Roles.CUSTOMER);
    this.updateAccounts();
  }

  /**
   * Updates the local list of accounts associated with the user.
   */
  private void updateAccounts() {
    this.accounts = new ArrayList<>();
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    try {
      for (int accountId : instance.getAccountIdsList(super.getId())) {
        this.accounts.add(instance.getAccountObject(accountId));
      }
    } catch(SQLException e) {
      System.out.println("Error adding accounts!");
    } finally {
      instance.close();
    }
  }

  /**
   * Returns a list of the customer's accounts.
   * @return List of customer's accounts
   */
  public List<Account> getAccounts() {
    return this.accounts;
  }

  /**
   * Add a new account for the customer.
   * @param account The new account
   */
  public void addAccount(Account account) {
    if (!this.accounts.contains(account)) {
      this.accounts.add(account);
    }
  }
}
