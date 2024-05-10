package me.syncwrld.streets1.auth.event;

import me.syncwrld.streets1.auth.memory.PendingPlayerMemory;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class ActionListener implements Listener {

  @EventHandler
  public void handleFastWalk(PlayerToggleSprintEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handleChat(AsyncPlayerChatEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handleInteraction(PlayerInteractEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handleConsume(PlayerItemConsumeEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handleDrop(PlayerDropItemEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handlePickup(PlayerPickupItemEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  @EventHandler
  public void handleEntityInteraction(PlayerInteractEntityEvent event) {
    caseCancel(event.getPlayer(), event);
  }

  private void caseCancel(Player player, PlayerEvent event) {
    boolean isPending = PendingPlayerMemory.INSTANCE.isPending(player);
    if (isPending) {
      ((Cancellable) event).setCancelled(true);
    }
  }
}
