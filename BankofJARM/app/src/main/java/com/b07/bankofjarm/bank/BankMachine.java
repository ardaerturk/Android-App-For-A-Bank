package com.b07.bankofjarm.bank;

import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.databasehelper.DatabaseUpdateHelper;
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.user.Customer;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public abstract class BankMachine {

  private Customer currentCustomer;

  /**
   * Updates the customer's balance to add a deposit
   *
   * @param amount is amount to deposit
   * @param accountId is id of the account to add deposit to
   * @return whether or not the deposit was successfully made
   * @throws SQLException if something goes wrong with the database
   */
  public boolean makeDeposit(BigDecimal amount, int accountId) throws SQLException {
    int userId = this.currentCustomer.getId();
    DatabaseSelectHelper instance3 = DatabaseSelectHelper.getInstance(null);
    List<Integer> listOfAccountIds = instance3.getAccountIdsList(userId);
    instance3.close();
    // Check if the user has access to this account
    if (listOfAccountIds.contains(accountId)) {
      BigDecimal big = new BigDecimal(0);
      // Check if the amount to deposit is negative
      if (amount.compareTo(big) != -1) {

        DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
        BigDecimal balance = instance.getBalance(accountId);
        instance.close();

        DatabaseUpdateHelper instance2 = DatabaseUpdateHelper.getInstance(null);
        boolean success = instance2.updateAccountBalance(balance.add(amount), accountId);
        instance2.close();
        return success;
      } else {
        System.out.println("You can't deposit a negative amount.");
        return false;
      }
    } else {
      System.out.println("You do not have access to this account.");
      return false;
    }
  }

  /**
   * Update's the customer's account to make withdrawal
   *
   * @param amount is amount to withdraw
   * @param accountId is account to withdraw from
   * @return whether or not the withdrawal was successful
   * @throws SQLException if something goes wrong with the database
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws SQLException {
    // TODO: implement db stuff with this huge ginormous block of if statements

    int userId = this.currentCustomer.getId();
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    List<Integer> listOfAccountIds = instance.getAccountIdsList(userId);
    // Check if the user has access to account
    if (listOfAccountIds.contains(accountId)) {
      BigDecimal big = new BigDecimal(0);
      int typeId = instance.getAccountType(accountId);
      // Check if amount to withdraw is negative
      if (amount.compareTo(big) != -1) {
        BigDecimal balance = instance.getBalance(accountId);
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(big) != -1) {
          instance.close();
          // Check if the account type is Savings and if the new balance < 1000
          int savingsId = AccountTypesMap.getInstance(null).getId(AccountTypes.SAVING);
          BigDecimal minSavings = new BigDecimal(1000);
          DatabaseUpdateHelper instance2 = DatabaseUpdateHelper.getInstance(null);
          if (savingsId == typeId && newBalance.compareTo(minSavings) == -1) {
            // If it is, update the type of the account to chequing
            instance2.updateAccountType(AccountTypesMap.getInstance(null).getId(AccountTypes.CHEQUING),
                accountId);
            instance2.close();
            this.currentCustomer.sendMessage(userId, "Your savings account with ID " + accountId
                + " no longer has the minimum balance, it is now a chequing account. \n");
            System.out.println("Your account has been changed to Chequing.");
          }
          boolean updated = instance2.updateAccountBalance(balance.subtract(amount), accountId);
          instance2.close();
          return updated;
        } else if (AccountTypesMap.getInstance(null).getId(AccountTypes.BALANCE_OWING) == typeId) {
          instance.close();
          // If the account type is BalanceOwing, the user can overdraw
          System.out.println("You now owe the bank -$" + newBalance.multiply(new BigDecimal(-1)));
          DatabaseUpdateHelper instance2 = DatabaseUpdateHelper.getInstance(null);
          boolean updated = instance2.updateAccountBalance(newBalance, accountId);
          instance2.close();
          return updated;
        } else {
          instance.close();
          System.out.println("You can't overdraw.");
          return false;
        }
      } else {
        instance.close();
        System.out.println("Nice try, but you can't withdraw a negative amount.");
        return false;
      }
    } else {
      instance.close();
      System.out.println("You do not have access to this account.");
      return false;
    }
  }

  /**
   * Check the balance of the given account
   *
   * @param accountId is the id of the account to check
   * @return the amount contained in the account if the user has access to it, null otherwise
   * @throws SQLException if there is a problem retrieving info from the database
   */
  public BigDecimal checkBalance(int accountId) throws SQLException {
    int userId = this.currentCustomer.getId();
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    List<Integer> listOfAccountIds = instance.getAccountIdsList(userId);


    // Check if the user has access to the account
    if (listOfAccountIds.contains(accountId)) {
      BigDecimal balance = instance.getBalance(accountId);
      instance.close();
      return balance;
    } else {
      System.out.println("You do not have access to this account.");
      instance.close();
      return null;
    }
  }

  /**
   * Gives the user a list of all their accounts
   *
   * @return a list of the customer's accounts
   */
  public List<Account> listAccounts() {
    return this.currentCustomer.getAccounts();
  }

  /**
   * Sets the customer for the current BankMachine
   *
   * @param customer is the current customer
   */
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
  }
}
