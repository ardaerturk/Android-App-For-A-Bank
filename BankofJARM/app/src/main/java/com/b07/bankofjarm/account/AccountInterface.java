package com.b07.bankofjarm.account;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface AccountInterface {
  
  public int getId();
	
  public void setId(int id);
	
  public String getName();
  
  public void setName(String name);
  
  public BigDecimal getBalance();
  
  public void setBalance(BigDecimal balance);
  
  public int getType();
  
  public void findAndSetInterestRate() throws SQLException;
  
  public void addInterest();
}
