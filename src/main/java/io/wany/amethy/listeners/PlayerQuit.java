package io.wany.amethy.listeners;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.PaperMessage;
import io.wany.amethy.modules.SpigotMessage;
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modules.sync.SyncConnection;
import io.wany.amethy.modules.wand.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    chatPlayerQuitMessage(event);
    playPlayerQuitSound(event);
    Wand.onPlayerQuit(event);
    Sync.onPlayerQuit(event);
  }

  private void chatPlayerQuitMessage(PlayerQuitEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.quit.msg.enable")) {
      return;
    }

    String format = Amethy.YAMLCONFIG.getString("event.quit.msg.format");

    if (Amethy.PAPERAPI) {
      event.quitMessage((format.equals("null") || Sync.getChat().isEnabled()) ? null : PaperMessage.Formatter.PLAYER.format(format, event.getPlayer()));
    }
    else {
      event.setQuitMessage((format == null || format.equals("null") || Sync.getChat().isEnabled()) ? null : SpigotMessage.Formatter.PLAYER.format(format, event.getPlayer()));
    }
  }

  private void playPlayerQuitSound(PlayerQuitEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.quit.sound.enable") || Sync.getChat().isEnabled()) {
      return;
    }

    Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.quit.sound.sound"));
    SoundCategory soundCategory = SoundCategory.valueOf(Amethy.YAMLCONFIG.getString("event.quit.sound.soundCategory"));
    float volume = (float) Amethy.YAMLCONFIG.getDouble("event.quit.sound.volume");
    float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.quit.sound.pitch");

    for (Player p : Bukkit.getOnlinePlayers()) {
      p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
    }
  }

}
