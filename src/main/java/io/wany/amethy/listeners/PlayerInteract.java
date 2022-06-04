package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import io.wany.amethy.itemonworld.ItemOnWorld;
import io.wany.amethy.wand.WandBrush;
import io.wany.amethy.wand.WandEdit;

public class PlayerInteract implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onPlayerInteract(PlayerInteractEvent event) {

    WandEdit.onPlayerInteract(event);
    WandBrush.onPlayerInteract(event);
    ItemOnWorld.onPlayerInteract(event);

  }

}
