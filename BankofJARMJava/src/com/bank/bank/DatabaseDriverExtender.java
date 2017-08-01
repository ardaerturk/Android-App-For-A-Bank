package com.bank.bank;

import com.bank.database.DatabaseDriver;
import com.bank.exceptions.ConnectionFailedException;

import java.sql.Connection;

public class DatabaseDriverExtender extends DatabaseDriver {
  public static Connection connectOrCreateDataBase() {
    return DatabaseDriver.connectOrCreateDataBase();
  }

  protected static Connection initialize(Connection connection) throws ConnectionFailedException {
    return DatabaseDriver.initialize(connection);
  }

  public static Connection reInitialize() throws ConnectionFailedException {
    return DatabaseDriver.reInitialize();
  }
}
