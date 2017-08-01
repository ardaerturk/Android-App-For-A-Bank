package com.b07.bankofjarm.account;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Account implements AccountInterface, Serializable {
  private static final long serialVersionUID = 3671339322709793021L;

  private transient int id;
  private String name;
  private BigDecimal balance;
  private AccountTypes type;
  private BigDecimal interestRate;

  public Account(int id, String name, BigDecimal balance, AccountTypes type) {
    this.id = id;
    this.name = name;
    this.balance = balance;
    this.type = type;
    this.interestRate = BigDecimal.ZERO;
  }

  /**
   * Returns the account id.
   * @return the id of the Account
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets the account id.
   * @param id the new id of the Account
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the account name.
   * @return the name of the Account
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the account name.
   * @param name the new name of the Account
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the account balance.
   * @return the balance of the Account
   */
  public BigDecimal getBalance() {
    return this.balance;
  }

  /**
   * Sets the account balance.
   * @param balance the new balance of the Account
   */
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  /**
   * Returns the account type.
   * @return the type of the Account
   */
  public int getType() {
    AccountTypesMap map = AccountTypesMap.getInstance(null);
    return map.getId(this.type);
  }

  /**
   * Sets the interest rate of the account by querying the database.
   * @throws SQLException if there is a problem with the query
   */
  public void findAndSetInterestRate() throws SQLException {
    DatabaseSelectHelper map = DatabaseSelectHelper.getInstance(null);
    this.interestRate = map.getInterestRate(this.getType());
  }

  /**
   * Returns the account type enum.
   * @return the type of the account
   */
  public AccountTypes getTypeEnum() {
    return this.type;
  }

  /**
   * Compounds the interest rate on the account by adding the current interest rate
   * to the account balance.
   */
  public void addInterest() {
    BigDecimal currentBalance = this.getBalance();
    currentBalance = currentBalance.add(currentBalance.multiply(this.interestRate));
    try {
      //DatabaseUpdateHelper.updateAccountBalance(currentBalance, this.getId());
      // TODO: fix when DatabaseUpdateHelper
      throw new SQLException();
    } catch (SQLException e) {

    }
    this.setBalance(currentBalance);
  }

  @Override
  public String toString() {
    return R.string.dialog_account_type + this.type.toString() + "\n" + R.string.dialog_account_name + this.name + "\n" + R.string.dialog_account_balance + this.balance.toString();
  }
}