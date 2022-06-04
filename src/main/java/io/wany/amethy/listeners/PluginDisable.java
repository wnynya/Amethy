package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import io.wany.amethy.supports.coreprotect.CoreProtectSupport;
import io.wany.amethy.supports.cucumbery.CucumberySupport;
import io.wany.amethy.supports.vault.VaultSupport;

public class PluginDisable implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onPluginDisable(PluginDisableEvent event) {

    VaultSupport.onPluginDisable(event);
    CucumberySupport.onPluginDisable(event);
    CoreProtectSupport.onPluginDisable(event);

  }

}
