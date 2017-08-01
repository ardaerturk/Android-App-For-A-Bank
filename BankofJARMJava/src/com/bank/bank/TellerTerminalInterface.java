package com.bank.bank;

import java.math.BigDecimal;
import java.sql.SQLException;
import com.bank.user.Customer;

public interface TellerTerminalInterface {

  public boolean makeNewAccount(String name, BigDecimal balance, int type) throws SQLException;

  public void setCurrentCustomer(Customer customer);

  public boolean authenticateCurrentCustomer(String password) throws SQLException;

  public void makeNewUser(String name, int age, String address, String password) throws SQLException;

  public void giveInterest(int accountId) throws SQLException;

  public void giveInterestToAccount(int accountID) throws SQLException;

  public void deAuthenticateCustomer();
}
