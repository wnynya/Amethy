package io.wany.amethy.listeners;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import io.wany.amethy.Amethy;

public class EntityDeath implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDeath(EntityDeathEvent event) {

    playMonsterKilledByPlayerSound(event);

  }

  public static void playMonsterKilledByPlayerSound(EntityDeathEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.monsterDeath.killer.sound.enable")) {
      return;
    }
    LivingEntity entity = event.getEntity();
    if (!(entity instanceof Monster)) {
      return;
    }
    Player killer = entity.getKiller();
    if (killer == null) {
      return;
    }
    Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.monsterDeath.killer.sound.sound"));
    SoundCategory soundCategory = SoundCategory
        .valueOf(Amethy.YAMLCONFIG.getString("event.monsterDeath.killer.sound.soundCategory"));
    float volume = (float) Amethy.YAMLCONFIG.getDouble("event.monsterDeath.killer.sound.volume");
    float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.monsterDeath.killer.sound.pitch");
    killer.playSound(killer.getLocation(), sound, soundCategory, volume, pitch);
  }

}
