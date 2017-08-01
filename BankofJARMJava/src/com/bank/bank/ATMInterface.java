package com.bank.bank;

import java.sql.SQLException;

import com.bank.user.Customer;

public interface ATMInterface {
	
  public boolean authenticate(int userId, String password) throws SQLException;
  
  public Customer getCurrentCustomer();
}
