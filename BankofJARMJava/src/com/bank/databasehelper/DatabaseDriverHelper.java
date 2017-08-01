package com.bank.databasehelper;

import java.sql.Connection;

import com.bank.database.DatabaseDriver;

public class DatabaseDriverHelper extends DatabaseDriver {
  public static Connection connectOrCreateDataBase() {
    return DatabaseDriver.connectOrCreateDataBase();
  }
}
