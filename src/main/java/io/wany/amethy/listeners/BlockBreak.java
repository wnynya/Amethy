package io.wany.amethy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public static void onBlockBreak(BlockBreakEvent event) {

  }

}
