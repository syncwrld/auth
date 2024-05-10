package me.syncwrld.streets1.auth.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import java.util.UUID;
import me.syncwrld.booter.libs.google.guava.base.Stopwatch;
import me.syncwrld.streets1.auth.AuthEngine;
import me.syncwrld.streets1.auth.controller.AuthTokenController;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;
import me.syncwrld.streets1.auth.model.player.PlayerAccount;
import me.syncwrld.streets1.auth.model.player.PlayerAccountData;
import org.bukkit.entity.Player;

@CommandAlias("registermany")
public class TestCommand extends BaseCommand {

  private final PlayerAccountMemory memory = PlayerAccountMemory.getInstance();
  private final AuthEngine engine;
  private final AuthTokenController authTokenController = new AuthTokenController();

  public TestCommand(AuthEngine engine) {
    this.engine = engine;
  }

  @Default
  @Syntax("<howMany>")
  @CommandPermission("auth.command.registermany")
  @Conditions("bePlayer")
  public void handleDefault(Player player, int howMany) {
    player.sendMessage("Registering " + howMany + " accounts...");
    Stopwatch stopwatch = Stopwatch.createStarted();

    for (int i = 0; i < howMany; i++) {
      PlayerAccountMemory.INSTANCE.registerAccount(
          new PlayerAccount(
              UUID.randomUUID(),
              "password",
              new PlayerAccountData(authTokenController.getRandomSalt(), 0, 0)));
    }

    stopwatch.stop();
    player.sendMessage("Registered " + howMany + " accounts on memory in " + stopwatch.toString() + "!");
  }
}
