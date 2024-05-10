package me.syncwrld.streets1.auth.controller;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AuthTokenController {

  private static final int R_TOKEN_LENGTH = 32;
  private static final String SALT_ALLOWED_CHARS =
      "{}__@@**()-_+!@#$%^&ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk{}__@@**()-_+!@#$%^&lmnopqrstuvwxyz0123456789{}__@@**()-_+!@#$%^&";
  private static final int ITERATIONS = 1000;
  private static final int KEY_LENGTH = 256;

  public String getRandomSalt() {
    StringBuilder salt = new StringBuilder();
    for (int i = 0; i < R_TOKEN_LENGTH; i++) {
      int rand = (int) (Math.random() * SALT_ALLOWED_CHARS.length());
      salt.append(SALT_ALLOWED_CHARS.charAt(rand));
    }
    return salt.toString();
  }

  public String encryptPassword(String password, String salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
    byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

    KeySpec spec = new PBEKeySpec(new String(passwordBytes).toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] encryptedBytes = factory.generateSecret(spec).getEncoded();

    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public boolean verifyPassword(String password, String salt, String encryptedPassword)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String encryptedPasswordAttempt = encryptPassword(password, salt);
    return encryptedPasswordAttempt.equals(encryptedPassword);
  }
}
