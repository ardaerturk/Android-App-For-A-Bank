package com.bank.bank;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import com.bank.account.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasemap.AccountTypesMap;
import com.bank.generics.AccountTypes;
import com.bank.user.Customer;

public abstract class BankMachine {

  private Customer currentCustomer;

  /**
   * Updates the customer's balance to add a deposit
   * @param amount is amount to deposit
   * @param accountId is id of the account to add deposit to
   * @return whether or not the deposit was successfully made
   * @throws SQLException if something goes wrong with the database
   */
  public boolean makeDeposit(BigDecimal amount, int accountId) throws SQLException {
    int userId = this.currentCustomer.getId();
    List<Integer> listOfAccountIds = DatabaseSelectHelper.getAccountIds(userId);
    // Check if the user has access to this account
    if (listOfAccountIds.contains(accountId)) {
      BigDecimal big = new BigDecimal(0);
      // Check if the amount to deposit is negative
      if (amount.compareTo(big) != -1) {
        BigDecimal balance = DatabaseSelectHelper.getBalance(accountId);
        return DatabaseUpdateHelper.updateAccountBalance(balance.add(amount), accountId);
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
   * @param amount is amount to withdraw
   * @param accountId is account to withdraw from
   * @return whether or not the withdrawal was successful
   * @throws SQLException if something goes wrong with the database
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws SQLException {
    int userId = this.currentCustomer.getId();
    List<Integer> listOfAccountIds = DatabaseSelectHelper.getAccountIds(userId);
    // Check if the user has access to account
    if (listOfAccountIds.contains(accountId)) {
      BigDecimal big = new BigDecimal(0);
      int typeId = DatabaseSelectHelper.getAccountType(accountId);
      // Check if amount to withdraw is negative
      if (amount.compareTo(big) != -1) {
        BigDecimal balance = DatabaseSelectHelper.getBalance(accountId);
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(big) != -1) {
          // Check if the account type is Savings and if the new balance < 1000
          int savingsId = AccountTypesMap.getId(AccountTypes.SAVING);
          BigDecimal minSavings = new BigDecimal(1000);
          if (savingsId == typeId && newBalance.compareTo(minSavings) == -1) {
            // If it is, update the type of the account to chequing
            DatabaseUpdateHelper.updateAccountType(AccountTypesMap.getId(AccountTypes.CHEQUING),
                    accountId);
            this.currentCustomer.sendMessage(userId, "Your savings account with ID " + accountId
                    + " no longer has the minimum balance, it is now a chequing account. \n" );
            System.out.println("Your account has been changed to Chequing.");
          }
          return DatabaseUpdateHelper.updateAccountBalance(balance.subtract(amount), accountId);
        } else if (AccountTypesMap.getId(AccountTypes.BALANCE_OWING) == typeId) {
          // If the account type is BalanceOwing, the user can overdraw
          System.out.println("You now owe the bank -$" + newBalance.multiply(new BigDecimal(-1)));
          return DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId);
        } else {
          System.out.println("You can't overdraw.");
          return false;
        }
      } else {
        System.out.println("Nice try, but you can't withdraw a negative amount.");
        return false;
      }
    } else {
      System.out.println("You do not have access to this account.");
      return false;
    }
  }

  /**
   * Check the balance of the given account
   * @param accountId is the id of the account to check
   * @return the amount contained in the account if the user has access to it, null otherwise
   * @throws SQLException if there is a problem retrieving info from the database
   */
  public BigDecimal checkBalance(int accountId) throws SQLException {
    int userId = this.currentCustomer.getId();
    List<Integer> listOfAccountIds = DatabaseSelectHelper.getAccountIds(userId);
    // Check if the user has access to the account
    if (listOfAccountIds.contains(accountId)) {
      return DatabaseSelectHelper.getBalance(accountId);
    } else {
      System.out.println("You do not have access to this account.");
      return null;
    }
  }

  /**
   * Gives the user a list of all their accounts
   * @return a list of the customer's accounts
   */
  public List<Account> listAccounts() {
    return this.currentCustomer.getAccounts();
  }

  /**
   * Sets the customer for the current BankMachine
   * @param customer is the current customer
   */
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
  }
}
