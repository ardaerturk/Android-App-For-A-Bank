package com.bank.databasemap;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.generics.AccountTypes;
import java.sql.SQLException;
import java.util.EnumMap;

/**
 * Created by jr on 16/07/17.
 */
public class AccountTypesMap {
  private static boolean initialized = false;
  private static EnumMap<AccountTypes, Integer> map = new EnumMap<AccountTypes, Integer>(AccountTypes.class);

  /**
   * Returns the id of a role in the database.
   * @param element The enum constant role
   * @return the roleId
   */
  public static int getId(AccountTypes element) {
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
      for (AccountTypes type : AccountTypes.values()) {
        for (int typeId : DatabaseSelectHelper.getAccountTypesIds()) {
          // Checks if role name matches role name in the database corresponding to id
          if (DatabaseSelectHelper.getAccountTypeName(typeId).equalsIgnoreCase(type.name())) {
            map.put(type, typeId);
          }
        }
      }
    } catch (SQLException e) {
      return false;
    }

    return true;
  }

}
