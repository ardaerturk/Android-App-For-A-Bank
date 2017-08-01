package com.bank.account;

import java.math.BigDecimal;

import com.bank.exceptions.NoSuchAccountException;
import com.bank.generics.AccountTypes;

public class AccountFactory {
  
  private static final String CHEQUING = "CHEQUING";
  private static final String SAVINGS = "SAVINGS";
  private static final String TFSA = "TFSA";
  private static final String RESTRICTED_SAVINGS = "RESTRICTED_SAVINGS";
  private static final String BALANCE_OWING = "BALANCE_OWING";
  
  /**
   * Returns an account of the given type.
   * @param accountType the type of the account.
   * @return an Account of given Type.
   * @throws NoSuchAccountException if given account is not a known type.
   */
  public static Account getAccount(String accountType, int id, String name, BigDecimal balance, AccountTypes type) throws NoSuchAccountException {
    if (accountType == null) {
      return null;
    } else if (accountType.equalsIgnoreCase(CHEQUING)) {
      return new Account(id, name, balance, type);
    } else if (accountType.equalsIgnoreCase(SAVINGS)) {
      return new Account(id, accountType, balance, type);
    } else if (accountType.equalsIgnoreCase(TFSA)) {
      return new Account(id, accountType, balance, type);
    } else if (accountType.equalsIgnoreCase(RESTRICTED_SAVINGS)) {
      return new Account(id, accountType, balance, type);
    } else if (accountType.equalsIgnoreCase(BALANCE_OWING)) {
      return new Account(id, accountType, balance, type);
    }
    throw new NoSuchAccountException();
  }

}
