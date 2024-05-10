package me.syncwrld.streets1.auth.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import me.syncwrld.streets1.auth.database.AuthDatabase;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;

public class AuthController {

  private final AuthDatabase database;
  private final AuthTokenController token = new AuthTokenController();

  public AuthController(AuthDatabase database) {
    this.database = database;
  }

  public boolean isRegistered(UUID uuid) {
    return database.isRegistered(uuid);
  }

  public boolean register(UUID uuid, String password)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (isRegistered(uuid)) {
      return false;
    }

    String salt = token.getRandomSalt();
    String encryptedPassword = token.encryptPassword(password, salt);
    database.registerAccount(uuid, salt, encryptedPassword);

    return true;
  }

  public boolean isPasswordValid(UUID uuid, String password, String salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    return PlayerAccountMemory.INSTANCE.isPasswordValid(
        password, salt, PlayerAccountMemory.INSTANCE.getEncryptedPasswordOf(uuid));
  }
}
