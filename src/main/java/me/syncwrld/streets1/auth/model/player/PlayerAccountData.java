package me.syncwrld.streets1.auth.model.player;

import lombok.Data;

@Data
public class PlayerAccountData {
    private final String saltToken;
    private final long firstLogin;
    private final long lastLogin;
}
