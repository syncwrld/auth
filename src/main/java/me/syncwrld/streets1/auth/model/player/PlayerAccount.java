package me.syncwrld.streets1.auth.model.player;

import lombok.Data;
import me.syncwrld.booter.minecraft.Serializable;

import java.util.UUID;

@Data
public class PlayerAccount implements Serializable {
  private final UUID uuid;
  private final String password;
  private final PlayerAccountData accountData;
}
