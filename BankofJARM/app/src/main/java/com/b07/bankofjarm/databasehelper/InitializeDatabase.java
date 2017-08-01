package com.b07.bankofjarm.databasehelper;

import android.content.Context;
import android.database.Cursor;
import com.b07.bankofjarm.database.DatabaseDriverA;
import com.b07.bankofjarm.generics.AccountTypes;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import java.math.BigDecimal;

/**
 * Created by jr on 30/07/17.
 */

public class InitializeDatabase extends DatabaseDriverA {
  private static InitializeDatabase singleton = null;
  private Context mContext;

  private InitializeDatabase(Context context) {
    super(context);
    this.mContext = context;
  }

  public static InitializeDatabase getInstance(Context context) {
    if (singleton == null) {
      singleton = new InitializeDatabase(context);
    }
    return singleton;
  }

  public void initialize() {
    // Initialize roles and account types
    initializeRolesTable();
    initializeAccountTypesTable();
    // Initialize admin
    initializeUsers();
  }

  public void reInitialize() {
    this.onUpgrade(this.getWritableDatabase(), 1, 1);
    initialize();
  }

  public void clearDatabase() {
    super.onUpgrade(this.getWritableDatabase(), 1, 1);
  }

  private void initializeRolesTable() {
    for (Roles role : Roles.values()) {
      super.insertRole(role.name());
    }
  }

  private void initializeAccountTypesTable() {
    for (AccountTypes type : AccountTypes.values()) {
      super.insertAccountType(type.name(), new BigDecimal(0.2));
    }
  }

  private void initializeUsers() {
    super.insertNewUser("Admin", 100, "UTSC", RolesMap.getInstance(null).getId(Roles.ADMIN), "pw");
    super.insertNewUser("Teller", 100, "UTSC", RolesMap.getInstance(null).getId(Roles.TELLER), "pw");
    int id = (int) super.insertNewUser("Customer", 100, "UTSC", RolesMap.getInstance(null).getId(Roles.CUSTOMER), "pw");

    int acc1 = (int) super.insertAccount("Customer's chequing account", new BigDecimal(100),
        AccountTypesMap.getInstance(null).getId(AccountTypes.CHEQUING));
    int acc2 = (int) super.insertAccount("Customer's tfsa account", new BigDecimal(100),
        AccountTypesMap.getInstance(null).getId(AccountTypes.TFSA));
    int acc3 = (int) super.insertAccount("Customer's saving account", new BigDecimal(100),
        AccountTypesMap.getInstance(null).getId(AccountTypes.SAVING));

    super.insertUserAccount(id, acc1);
    super.insertUserAccount(id, acc2);
    super.insertUserAccount(id, acc3);
  }
}
