package me.syncwrld.streets1.auth.task;

import com.cryptomorin.xseries.messages.ActionBar;
import me.syncwrld.streets1.auth.AuthEngine;
import me.syncwrld.streets1.auth.Configuration;
import me.syncwrld.streets1.auth.memory.PendingPlayerMemory;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class ExpiryTimeConstantTask implements Runnable {
  private final PendingPlayerMemory playerMemory = PendingPlayerMemory.INSTANCE;

  private final AuthEngine authEngine;

  public ExpiryTimeConstantTask(AuthEngine authEngine) {
    this.authEngine = authEngine;
  }

  @Override
  public void run() {
    Iterator<Player> iterator = playerMemory.getPendingPlayers().iterator();
    while (iterator.hasNext()) {
      Player pendingPlayer = iterator.next();

      long lastTime = playerMemory.getJoinTime(pendingPlayer);
      long secondsAgoLastTime = Math.abs((System.currentTimeMillis() - lastTime) / 1000L);

      ActionBar.sendActionBar(pendingPlayer, "Remaining time to login: " + (Configuration.MAX_LOGIN_WAIT_TIME - secondsAgoLastTime) + " seconds.");

      if (secondsAgoLastTime >= Configuration.MAX_LOGIN_WAIT_TIME) {
        iterator.remove();
        pendingPlayer.playSound(
                pendingPlayer.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);

        Bukkit.getScheduler()
                .runTaskLater(this.authEngine, () -> pendingPlayer.kickPlayer("Your login has expired."), 30);
      }
    }
  }
}
