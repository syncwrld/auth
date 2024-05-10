package me.syncwrld.streets1.auth.event;

import com.cryptomorin.xseries.messages.ActionBar;
import me.syncwrld.streets1.auth.memory.PendingPlayerMemory;
import me.syncwrld.streets1.auth.memory.PlayerAccountMemory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class ConnectionTrafficListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handleConnection(PlayerLoginEvent event) {
    Player player = event.getPlayer();
    if (PlayerAccountMemory.INSTANCE.isRegistered(player.getUniqueId())) {
      ActionBar.sendActionBar(player, "§aPlease use /login <password>");
    } else {
      ActionBar.sendActionBar(player, "§aPlease use /register <password> <password>");
    }

    PendingPlayerMemory.INSTANCE.addPendingPlayer(player);
  }
}
