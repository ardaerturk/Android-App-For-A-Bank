package com.b07.bankofjarm.user;

import com.b07.bankofjarm.generics.Roles;

/**
 * Created by jr on 21/06/17.
 */
public class Admin extends User {

  private static final long serialVersionUID = 1238393026153201217L;

  /**
   * Initializes an admin, without authentication.
   * @param id The id of the Admin
   * @param name The full name of the Admin
   * @param age The age of the admin in years
   * @param address The address of the admin
   */
  public Admin(int id, String name, int age, String address) {
    this(id, name, age, address, false);
  }

  /**
   * Initializes an admin.
   * @param id The id of the Admin
   * @param name The full name of the Admin
   * @param age The age of the admin in years
   * @param address The address of the admin
   */
  public Admin(int id, String name, int age, String address, boolean authenticated) {
    super(id, name, age, address, authenticated, Roles.ADMIN);
  }
}
