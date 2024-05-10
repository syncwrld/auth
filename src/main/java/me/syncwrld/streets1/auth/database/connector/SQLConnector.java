package me.syncwrld.streets1.auth.database.connector;

import java.io.File;
import me.syncwrld.booter.database.DatabaseCredentials;
import me.syncwrld.booter.database.connector.SimpleDatabaseConnector;
import me.syncwrld.booter.database.connector.sample.DatabaseType;
import me.syncwrld.streets1.auth.AuthEngine;
import me.syncwrld.streets1.auth.database.AuthDatabase;

public class SQLConnector {

  public AuthDatabase connect(AuthEngine engine) {
    DatabaseType databaseType =
        getDatabaseType(engine.getConfiguration().getString("database.type"));
    SimpleDatabaseConnector connector;

    switch (databaseType) {
      case MYSQL_HIKARICP:
      case MYSQL:
        connector =
            new SimpleDatabaseConnector(
                databaseType, DatabaseCredentials.withConfiguration(engine.getConfiguration()));
        break;
      case SQLITE:
        File sqliteFile =
            new File(
                engine.getDataFolder(),
                engine.getConfiguration().getString("database.sqlite-file"));
        connector = new SimpleDatabaseConnector(databaseType, sqliteFile);
        break;
      default:
        throw new IllegalArgumentException("Invalid database type: " + databaseType);
    }

    if (!connector.connect()) {
      engine.log("&eDATABASE! &4Failed to connect to database.");
      return null;
    }

    engine.log(
        "&eDATABASE! &aSuccessfully connected to database. (" + databaseType.getName() + ")");
    return new AuthDatabase(connector.getConnection());
  }

  private DatabaseType getDatabaseType(String type) {
    switch (type.toUpperCase()) {
      case "MYSQL":
        return DatabaseType.MYSQL;
      case "MYSQL-HIKARI":
        return DatabaseType.MYSQL_HIKARICP;
      case "SQLITE":
        return DatabaseType.SQLITE;
      default:
        throw new IllegalArgumentException("Invalid database type: " + type);
    }
  }
}
