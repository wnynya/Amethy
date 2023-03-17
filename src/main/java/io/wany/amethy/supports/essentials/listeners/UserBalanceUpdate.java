package io.wany.amethy.supports.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.wany.amethy.modules.sync.SyncVaultEconomy;
import net.ess3.api.events.UserBalanceUpdateEvent;

public class UserBalanceUpdate implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void on(UserBalanceUpdateEvent event) {

    SyncVaultEconomy.onUserBalanceUpdte(event.getPlayer(), event.getNewBalance().doubleValue());

  }

}
