package me.syncwrld.streets1.auth.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import lombok.val;
import me.syncwrld.streets1.auth.controller.AuthTokenController;
import me.syncwrld.streets1.auth.memory.PendingPlayerMemory;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;
import me.syncwrld.streets1.auth.model.player.PlayerAccount;
import me.syncwrld.streets1.auth.model.player.PlayerAccountData;
import org.bukkit.entity.Player;

@CommandAlias("register|registrar|registro|rg")
public class RegisterCommand extends BaseCommand {

  private final AuthTokenController authTokenController = new AuthTokenController();
  private final PlayerAccountMemory memory = PlayerAccountMemory.getInstance();

  @Default
  @CommandPermission("auth.command.register")
  @Syntax("<password> <password>")
  @Conditions("bePlayer")
  public void handleDefault(Player player, String[] arguments) {
    UUID uniqueId = player.getUniqueId();

    if (memory.isRegistered(uniqueId)) {
      player.sendMessage("§cYou are already registered!");
      return;
    }

    if (arguments.length != 2) {
      player.sendMessage("§cUsage: /register <password> <password>");
      return;
    }

    String password_1 = arguments[0];
    String password_2 = arguments[1];

    if (!password_1.equals(password_2)) {
      player.sendMessage("§cPasswords do not match!");
      return;
    }

    val password = password_1;

    if (password.length() < 8) {
      player.sendMessage("§cPassword must be at least 8 characters long!");
      return;
    }

    String randomSalt = authTokenController.getRandomSalt();
    String encryptedPassword = null;

    try {
      encryptedPassword = authTokenController.encryptPassword(password, randomSalt);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }

    memory.registerAccount(
        new PlayerAccount(
            uniqueId,
            encryptedPassword,
            new PlayerAccountData(
                randomSalt, System.currentTimeMillis(), System.currentTimeMillis())));
    memory.loadEncryptedPasswordOf(uniqueId, encryptedPassword);

    player.sendMessage("§aYou have successfully registered!");
    PendingPlayerMemory.INSTANCE.removePendingPlayer(player);
  }
}
