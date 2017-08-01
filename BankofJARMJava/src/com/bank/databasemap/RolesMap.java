package com.bank.databasemap;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import java.sql.SQLException;
import java.util.EnumMap;

/**
 * Created by jr on 14/07/17.
 */
public class RolesMap {
  private static boolean initialized = false;
  private static EnumMap<Roles, Integer> map = new EnumMap<Roles, Integer>(Roles.class);

  /**
   * Returns the id of a role in the database.
   * @param element The enum constant role
   * @return the roleId
   */
  public static int getId(Roles element) {
    if (!initialized) {
      update();
      initialized = true;
    }
    return map.get(element);
  }

  /**
   * Query the database and update roles map values.
   * @return true if map was successfully updated
   */
  public static boolean update() {
    map.clear();
    try {
      for (Roles role : Roles.values()) {
        for (int roleId : DatabaseSelectHelper.getRoles()) {
          // Checks if role name matches role name in the database corresponding to id
          if (DatabaseSelectHelper.getRole(roleId).equalsIgnoreCase(role.name())) {
            map.put(role, roleId);
          }
        }
      }
    } catch (SQLException e) {
      return false;
    }

    return true;
  }

}
