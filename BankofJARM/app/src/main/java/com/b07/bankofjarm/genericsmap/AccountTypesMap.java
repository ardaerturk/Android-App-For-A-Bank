package com.b07.bankofjarm.genericsmap;

import android.content.Context;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.AccountTypes;
import java.util.EnumMap;

/**
 * Created by jr on 14/07/17.
 */
public class AccountTypesMap implements GenericsMap<AccountTypes> {

  private EnumMap<AccountTypes, Integer> map;

  private static AccountTypesMap singleton = null;

  /**
   * Sets up the role map singleton
   *
   * @param context Application context, needed to query the database
   */
  private AccountTypesMap(Context context) {
    this.map = new EnumMap<>(AccountTypes.class);
    this.update(context);
  }

  /**
   * Return an instance of a roles map.
   *
   * @param context The current application context
   * @return The singleton instance
   */
  public static AccountTypesMap getInstance(Context context) {
    if (singleton == null) {
      singleton = new AccountTypesMap(context);
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
  public int getId(AccountTypes element) {
    return this.map.get(element);
  }

  /**
   * Query the database and update roles map values.
   */
  @Override
  public void update(Context context) {
    this.map.clear();
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(context);
    for (AccountTypes type : AccountTypes.values()) {
      for (int typeId : instance.getAccountTypesIdList()) {
        // Checks if type name matches type name in the database corresponding to id
        if (instance.getAccountTypeName(typeId).equalsIgnoreCase(type.name())) {
          this.map.put(type, typeId);
        }
      }
    }
  }
}
