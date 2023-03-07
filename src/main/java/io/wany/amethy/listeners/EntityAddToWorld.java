package io.wany.amethy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.wany.amethy.Amethy;

public class EntityAddToWorld implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityAddToWorld(EntityAddToWorldEvent event) {

    fixPrimedTNTVelocity(event);

  }

  private static void fixPrimedTNTVelocity(EntityAddToWorldEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.tntPrimed.fixVelocity")) {
      return;
    }
    Entity entity = event.getEntity();
    if (entity.getType().equals(EntityType.PRIMED_TNT)) {
      entity.setVelocity(entity.getVelocity().setX(0).setY(0).setZ(0));
    }
  }

}
