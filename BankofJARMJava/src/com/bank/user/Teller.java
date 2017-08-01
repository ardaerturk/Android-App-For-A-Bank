package com.bank.user;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.generics.Roles;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jr on 21/06/17.
 */
public class Teller extends User {

  private static final long serialVersionUID = -8377093601860056600L;

  /**
   * Initializes a teller, without authentication.
   * @param id The id of the Teller
   * @param name The full name of the Teller
   * @param age The age of the teller in years
   * @param address The address of the teller
   */
  public Teller(int id, String name, int age, String address) {
    this(id, name, age, address, false);
  }

  /**
   * Initializes a teller.
   * @param id The id of the Teller
   * @param name The full name of the Teller
   * @param age The age of the teller in years
   * @param address The address of the teller
   */
  public Teller(int id, String name, int age, String address, boolean authenticated) {
    super(id, name, age, address, authenticated, Roles.TELLER);
  }
}
