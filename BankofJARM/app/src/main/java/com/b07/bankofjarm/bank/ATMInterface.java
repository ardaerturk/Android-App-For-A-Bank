package com.b07.bankofjarm.bank;

import com.b07.bankofjarm.user.Customer;
import java.sql.SQLException;


public interface ATMInterface {
	
  public boolean authenticate(int userId, String password) throws SQLException;
  
  public Customer getCurrentCustomer();
}
