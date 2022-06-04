package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import io.wany.amethy.supports.coreprotect.CoreProtectSupport;
import io.wany.amethy.supports.cucumbery.CucumberySupport;
import io.wany.amethy.supports.vault.VaultSupport;

public class PluginEnable implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onPluginEnable(PluginEnableEvent event) {

    VaultSupport.onPluginEnable(event);
    CucumberySupport.onPluginEnable(event);
    CoreProtectSupport.onPluginEnable(event);

  }

}
