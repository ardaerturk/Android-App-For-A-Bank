package com.bank.user;

import com.bank.exceptions.NoSuchUserException;
import com.bank.generics.Roles;

public class UserFactory {
  
  private static final String ADMIN = "ADMIN";
  private static final String CUSTOMER = "CUSTOMER";
  private static final String TELLER = "TELLER";
  
  /**
   * Returns a user of the given type.
   * @param userType the type of the account.
   * @return a User of given Type.
   * @throws NoSuchUserException if given user is not a known type.
   */
  public static User getUser(String userType, int id, String name, int age, String address, int role, boolean authenticated) throws NoSuchUserException {
    if (userType == null) {
      return null;
    } else if (userType.equalsIgnoreCase(ADMIN)) {
      return new Admin(id, name, age, address, authenticated);
    } else if (userType.equalsIgnoreCase(CUSTOMER)) {
      return new Customer(id, name, age, address, authenticated);
    } else if (userType.equalsIgnoreCase(TELLER)) {
      return new Teller(id, name, age, address, authenticated);
    } 
    throw new NoSuchUserException();
  }
}
