package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import io.wany.amethy.supports.PluginSupport;

public class PluginDisable implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onPluginDisable(PluginDisableEvent event) {

    PluginSupport.onPluginEvent(event);

  }

}
