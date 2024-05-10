package me.syncwrld.streets1.auth.memory;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.bukkit.entity.Player;


@Getter
public class PendingPlayerMemory {
  public static PendingPlayerMemory INSTANCE = new PendingPlayerMemory();

  private final HashSet<Player> pendingPlayers = new HashSet<>();
  private final ConcurrentHashMap<Player, Long> timeMap = new ConcurrentHashMap<>();

  public void addPendingPlayer(Player player) {
    this.pendingPlayers.add(player);
    this.timeMap.put(player, System.currentTimeMillis());
  }

  public boolean isPending(Player player) {
    return this.pendingPlayers.contains(player);
  }

  public void removePendingPlayer(Player player) {
    this.pendingPlayers.remove(player);
    this.timeMap.remove(player);
  }

  public long getJoinTime(Player player) {
    return this.timeMap.get(player);
  }

}
