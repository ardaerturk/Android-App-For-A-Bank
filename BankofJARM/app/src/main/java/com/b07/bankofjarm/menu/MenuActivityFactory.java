package com.b07.bankofjarm.menu;

import android.content.Context;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.RolesMap;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jr on 28/07/17.
 */

public class MenuActivityFactory {

  /**
   * Returns the activity to redirect the login screen to by checking the user type and checking
   * if the user's password is correct.
   *
   * @param userId The numeric id of the user
   * @param context The application context
   * @return null if login is invalid, the class otherwise
   */
  public static Class getActivity(int userId, Context context) {
    // Get role of the user
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(context);
    int role = -1;
    try {
      role = instance.getUserRole(userId);
    } catch (Exception e) {
      return null;
    } finally {
      instance.close();
    }

    // Check role of the user
    RolesMap map = RolesMap.getInstance(context);
    if (role == map.getId(Roles.ADMIN)) {
      return AdminMenuActivity.class;
    } else if (role == map.getId(Roles.TELLER)) {
      return TellerMenuActivity.class;
    } else if (role == map.getId(Roles.CUSTOMER)) {
      return CustomerMenuActivity.class;
    }

    return null;
  }
}
