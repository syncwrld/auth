package me.syncwrld.streets1.auth.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import java.util.HashMap;
import java.util.UUID;
import me.syncwrld.streets1.auth.Configuration;
import me.syncwrld.streets1.auth.controller.AuthTokenController;
import me.syncwrld.streets1.auth.memory.PendingPlayerMemory;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;
import me.syncwrld.streets1.auth.model.player.PlayerAccount;
import me.syncwrld.streets1.auth.model.player.PlayerAccountData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@CommandAlias("login|logar")
public class LoginCommand extends BaseCommand {

  private final AuthTokenController authTokenController = new AuthTokenController();
  private final PlayerAccountMemory memory = PlayerAccountMemory.getInstance();
  private final HashMap<Player, Integer> attempts = new HashMap<>();

  @Default
  @CommandPermission("auth.command.login")
  @Syntax("<password>")
  @Conditions("bePlayer")
  public void handleDefault(Player player, String password) {
    UUID uniqueId = player.getUniqueId();

    if (!memory.isRegistered(uniqueId)) {
      player.sendMessage("§cYou must be registered!");
      return;
    }

    PlayerAccount playerAccount = memory.getAccount(uniqueId);

    PlayerAccountData accountData = playerAccount.getAccountData();
    if (!memory.isPasswordValid(
        password, accountData.getSaltToken(), playerAccount.getPassword())) {
      player.sendMessage("§cWrong password!");
      this.attempts.compute(player, (k, v) -> v == null ? 1 : v + 1);

      if (this.attempts.get(player) >= Configuration.MAX_LOGIN_ATTEMPTS) {
        player.kickPlayer("§cToo many login attempts!");
        this.attempts.remove(player);
      }

      return;
    }

    this.attempts.remove(player);

    PendingPlayerMemory.INSTANCE.removePendingPlayer(player);
    player.sendMessage("§aYou have successfully logged in!");
    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
  }
}
