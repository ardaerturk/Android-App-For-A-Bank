package com.b07.bankofjarm.genericsmap;

import android.content.Context;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import java.util.EnumMap;

/**
 * Created by jr on 14/07/17.
 */
public class RolesMap implements GenericsMap<Roles> {

  private EnumMap<Roles, Integer> map;

  private static RolesMap singleton = null;

  /**
   * Sets up the role map singleton
   *
   * @param context Application context, needed to query the database
   */
  private RolesMap(Context context) {
    this.map = new EnumMap<>(Roles.class);
    this.update(context);
  }

  /**
   * Return an instance of a roles map.
   *
   * @param context The current application context
   * @return The singleton instance
   */
  public static RolesMap getInstance(Context context) {
    if (singleton == null) {
      singleton = new RolesMap(context);
    }
    return singleton;
  }

  /**
   * Returns the id of a role in the database.
   *
   * @param element The enum constant role
   * @return the roleId
   */
  @Override
  public int getId(Roles element) {
    return this.map.get(element);
  }

  /**
   * Query the database and update roles map values.
   */
  @Override
  public void update(Context context) {
    this.map.clear();
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(context);
    for (Roles role : Roles.values()) {
      for (int roleId : instance.getRolesList()) {
        // Checks if role name matches role name in the database corresponding to id
        if (instance.getRole(roleId).equalsIgnoreCase(role.name())) {
          this.map.put(role, roleId);
        }
      }
    }
  }
}
