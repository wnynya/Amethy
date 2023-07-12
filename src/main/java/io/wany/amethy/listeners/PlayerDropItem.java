package io.wany.amethy.listeners;

import io.wany.amethy.modules.sync.Sync;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener {

  public static void onPlayerDropItem(PlayerDropItemEvent event) {

    Sync.onPlayerDropItem(event);

  }

}
