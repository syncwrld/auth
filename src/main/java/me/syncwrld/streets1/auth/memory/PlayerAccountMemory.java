package me.syncwrld.streets1.auth.memory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.syncwrld.booter.libs.google.guava.base.Stopwatch;
import me.syncwrld.streets1.auth.AuthEngine;
import me.syncwrld.streets1.auth.controller.AuthTokenController;
import me.syncwrld.streets1.auth.database.AuthDatabase;
import me.syncwrld.streets1.auth.model.player.PlayerAccount;

public class PlayerAccountMemory {

  public static final PlayerAccountMemory INSTANCE = new PlayerAccountMemory();
  private final ConcurrentHashMap<UUID, PlayerAccount> ACCOUNTS = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, String> ENCRYPTED_PASSWORDS = new ConcurrentHashMap<>();
  private final AuthTokenController AUTH_TOKEN_CONTROLLER = new AuthTokenController();

  public static PlayerAccountMemory getInstance() {
    return INSTANCE;
  }

  public boolean isRegistered(UUID uuid) {
    return ACCOUNTS.containsKey(uuid);
  }

  public PlayerAccount getAccount(UUID uuid) {
    return ACCOUNTS.get((uuid));
  }

  public void registerAccount(PlayerAccount account) {
    ACCOUNTS.put(account.getUuid(), account);
  }

  public void unregisterAccount(String uuid) {
    ACCOUNTS.remove(UUID.fromString(uuid));
  }

  public boolean isPasswordValid(String password, String salt, String encryptedPassword) {
    try {
      return AUTH_TOKEN_CONTROLLER.verifyPassword(password, salt, encryptedPassword);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadEncryptedPasswordOf(UUID uuid, String encryptedPassword) {
    ENCRYPTED_PASSWORDS.put(uuid, encryptedPassword);
  }

  public String getEncryptedPasswordOf(UUID uuid) {
    return ENCRYPTED_PASSWORDS.get(uuid);
  }

  public void saveDatabaseToMemory(AuthEngine engine) {
    engine.log("Saving accounts to memory. This may take a while...");
    Stopwatch stopwatch = Stopwatch.createStarted();
    AuthDatabase database = engine.getAuthDatabase();

    HashSet<PlayerAccount> accounts = database.getAccounts();
    for (PlayerAccount account : accounts) {
      registerAccount(account);
      loadEncryptedPasswordOf(account.getUuid(), account.getPassword());
    }

    stopwatch.stop();
    engine.log("Saved " + accounts.size() + " accounts to memory in " + stopwatch.toString() + "!");
  }

  public PlayerAccount getAccountOf(UUID uuid) {
    return ACCOUNTS.get(uuid);
  }

  public void saveMemoryToDatabase(AuthEngine engine) {
    engine.log("Saving accounts to database. This may take a while...");
    AuthDatabase database = engine.getAuthDatabase();
    Stopwatch stopwatch = Stopwatch.createStarted();

    int totalAccounts = ACCOUNTS.size();
    int accountsSaved = 0;
    int progressPercentage = 0;
    int progressBarLength = 20; // Length of the progress bar

    for (PlayerAccount account : ACCOUNTS.values()) {
      if (database.isRegistered(account.getUuid())) {
        database.updateAccount(
                account.getUuid(), account.getPassword(), getEncryptedPasswordOf(account.getUuid()));
      } else {
        database.registerAccount(
                account.getUuid(), account.getPassword(), getEncryptedPasswordOf(account.getUuid()));
      }
      accountsSaved++;
      int newProgressPercentage = (int) (((double) accountsSaved / totalAccounts) * 100);
      if (newProgressPercentage != progressPercentage) {
        progressPercentage = newProgressPercentage;
        int progressBarProgress = (int) (((double) accountsSaved / totalAccounts) * progressBarLength);
        StringBuilder progressBarBuilder = new StringBuilder();
        progressBarBuilder.append("&e[");
        for (int i = 0; i < progressBarProgress; i++) {
          progressBarBuilder.append("&a=&f");
        }
        for (int i = progressBarProgress; i < progressBarLength; i++) {
          progressBarBuilder.append(" ");
        }
        progressBarBuilder.append("&e]");
        engine.log("Progress: " + progressPercentage + "% " + progressBarBuilder.toString());
      }
    }

    stopwatch.stop();
    engine.log("Saved " + ACCOUNTS.size() + " accounts to database in " + stopwatch.toString() + "!");
  }


}
