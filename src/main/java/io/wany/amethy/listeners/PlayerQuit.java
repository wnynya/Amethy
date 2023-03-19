package io.wany.amethy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modules.sync.SyncConnection;
import io.wany.amethy.modules.wand.Wand;
import io.wany.amethy.modules.MsgUtil;

import java.util.HashMap;

public class PlayerQuit implements Listener {

  public static HashMap<String, String> changeQuitPlayers = new HashMap<>();

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

    event.quitMessage((format.equals("null") || SyncConnection.ENABLED) ? null
        : MsgUtil.formatPlayer(
            format,
            event.getPlayer()));
  }

  private void playPlayerQuitSound(PlayerQuitEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.quit.sound.enable") || SyncConnection.ENABLED) {
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
