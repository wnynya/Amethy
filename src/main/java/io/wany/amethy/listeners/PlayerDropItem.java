package io.wany.amethy.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import io.wany.amethy.modules.sync.SyncPlayer;

public class PlayerDropItem implements Listener {

  public static void onPlayerDropItem(PlayerDropItemEvent event) {
    SyncPlayer.onEvent(event);
  }

}
