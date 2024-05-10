package me.syncwrld.streets1.auth.extend;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;

public class CommandConditions {

  public static void register(PaperCommandManager commandManager) {
    commandManager
        .getCommandConditions()
        .addCondition(
            "bePlayer",
            (sender) -> {
              if (sender.getIssuer().getPlayer() == null)
                throw new ConditionFailedException("This command can only be used by players.");
            });
  }
}
