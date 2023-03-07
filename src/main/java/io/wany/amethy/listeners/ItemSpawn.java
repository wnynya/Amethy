package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

import io.wany.amethy.modules.itemonworld.ItemOnWorld;

public class ItemSpawn implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onItemSpawn(ItemSpawnEvent event) {

    ItemOnWorld.onItemSpawn(event);

  }

}
