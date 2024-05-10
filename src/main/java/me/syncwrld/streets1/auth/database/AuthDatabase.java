package me.syncwrld.streets1.auth.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import me.syncwrld.booter.Constants;
import me.syncwrld.booter.database.DatabaseHelper;
import me.syncwrld.booter.database.IdentifiableRepository;
import me.syncwrld.booter.database.TableComponent;
import me.syncwrld.streets1.auth.model.player.PlayerAccount;
import me.syncwrld.streets1.auth.model.player.PlayerAccountData;

public class AuthDatabase implements IdentifiableRepository, DatabaseHelper {

  @Getter(AccessLevel.PUBLIC)
  private final Connection connection;

  public AuthDatabase(Connection connection) {
    this.connection = connection;
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public boolean hasMoreThanOneTable() {
    return false;
  }

  @Override
  public void createTables() {
    this.createTable(
        this.connection,
        "s1_auth",
        new TableComponent(TableComponent.Type.VARCHAR_64, "uuid", true),
        new TableComponent(TableComponent.Type.TEXT, "password", false),
        new TableComponent(TableComponent.Type.JSON, "props", false));
  }

  public boolean isRegistered(UUID uuid) {
    return get("s1_auth", this.connection, "password", "uuid", uuid.toString(), String.class)
        != null;
  }

  @Override
  public Map<String, Integer> getTableIDs() {
    return Collections.singletonMap("s1_auth", 0);
  }

  public void registerAccount(UUID uuid, String salt, String encryptedPassword) {
    String serializedData =
        Constants.GSON.toJson(
            new PlayerAccountData(salt, System.currentTimeMillis(), System.currentTimeMillis()),
            PlayerAccountData.class);
    this.insert("s1_auth", this.connection, uuid.toString(), encryptedPassword, serializedData);
  }

  public void updateAccount(UUID uuid, String salt, String encryptedPassword) {
    String serializedData =
        Constants.GSON.toJson(
            new PlayerAccountData(salt, System.currentTimeMillis(), System.currentTimeMillis()),
            PlayerAccountData.class);

    String query =
        String.format(
            "update s1_auth set password = '%s', props = '%s' where uuid = '%s'",
            encryptedPassword, serializedData, uuid.toString());
    try (PreparedStatement statement = this.prepare(this.connection, query)) {
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public PlayerAccountData getAccountData(UUID uuid) {
    String serializedData =
        get("s1_auth", this.connection, "props", "uuid", uuid.toString(), String.class);
    if (serializedData == null) {
      return null;
    }
    return Constants.GSON.fromJson(serializedData, PlayerAccountData.class);
  }

  public HashSet<PlayerAccount> getAccounts() {
    HashSet<PlayerAccount> accounts = new HashSet<>();
    try (ResultSet result = this.result(this.prepare(this.connection, "select * from s1_auth"))) {
      while (result.next()) {
        PlayerAccount account =
            new PlayerAccount(
                UUID.fromString(result.getString("uuid")),
                result.getString("password"),
                this.getAccountData(UUID.fromString(result.getString("uuid"))));
        accounts.add(account);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return accounts;
  }
}
