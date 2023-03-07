package io.wany.amethy.supports.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.wany.amethy.modules.sync.SyncVaultEconomy;
import io.wany.amethy.modulesmc.Message;
import net.ess3.api.events.UserBalanceUpdateEvent;

public class UserBalanceUpdate implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void on(UserBalanceUpdateEvent event) {

    Bukkit.broadcast(Message.of(event.getPlayer().getName() + ": " + event.getOldBalance() + " ->"
        + event.getNewBalance() + " / " + event.getCause()));

    SyncVaultEconomy.onUserBalanceUpdte(event.getPlayer(), event.getNewBalance().doubleValue());

  }

}
