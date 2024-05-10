package me.syncwrld.streets1.auth;

import co.aikar.commands.PaperCommandManager;
import lombok.AccessLevel;
import lombok.Getter;
import me.syncwrld.booter.minecraft.loader.BukkitPlugin;
import me.syncwrld.streets1.auth.command.LoginCommand;
import me.syncwrld.streets1.auth.command.RegisterCommand;
import me.syncwrld.streets1.auth.command.TestCommand;
import me.syncwrld.streets1.auth.database.AuthDatabase;
import me.syncwrld.streets1.auth.database.connector.SQLConnector;
import me.syncwrld.streets1.auth.event.ActionListener;
import me.syncwrld.streets1.auth.event.ConnectionTrafficListener;
import me.syncwrld.streets1.auth.extend.CommandConditions;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;
import me.syncwrld.streets1.auth.task.ExpiryTimeConstantTask;
import org.bukkit.configuration.file.FileConfiguration;

@Getter(AccessLevel.PUBLIC)
public final class AuthEngine extends BukkitPlugin {

  private FileConfiguration configuration;
  private AuthDatabase authDatabase;

  @Override
  protected void whenLoad() {
    this.saveConfig();
    this.configuration = this.getConfigOf("configuration");

    this.setPrefix("&e[Auth]&f");
  }

  @Override
  protected void whenEnable() {
    this.authDatabase = new SQLConnector().connect(this);
    this.authDatabase.createTables();

    this.registerListeners(new ConnectionTrafficListener(), new ActionListener());
    this.startRepeatingRunnable(new ExpiryTimeConstantTask(this), 20);

    PaperCommandManager commandManager = new PaperCommandManager(this);
    CommandConditions.register(commandManager);

    commandManager.registerCommand(new LoginCommand());
    commandManager.registerCommand(new RegisterCommand());
    commandManager.registerCommand(new TestCommand(this));

    PlayerAccountMemory.INSTANCE.saveDatabaseToMemory(this);
  }

  @Override
  protected void whenDisable() {
    PlayerAccountMemory.INSTANCE.saveMemoryToDatabase(this);
  }
}
