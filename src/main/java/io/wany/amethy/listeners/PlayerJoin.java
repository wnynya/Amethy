package io.wany.amethy.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.commands.BungeeTeleportCommand;
import io.wany.amethy.modules.MsgUtil;
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modules.sync.SyncConnection;
import io.wany.amethy.modules.wand.Wand;

import java.util.HashMap;

public class PlayerJoin implements Listener {

  public static HashMap<String, String> changeJoinPlayers = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    chatPlayerJoinMessage(event);
    playPlayerJoinSound(event);
    Sync.onPlayerJoin(event);
    Wand.onPlayerJoin(event);
    BungeeTeleportCommand.onPlayerJoin(event);
  }

  private void chatPlayerJoinMessage(PlayerJoinEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.join.msg.enable")) {
      return;
    }

    String format = Amethy.YAMLCONFIG.getString("event.join.msg.format");

    event.joinMessage((format.equals("null") || SyncConnection.ENABLED) ? null
        : MsgUtil.formatPlayer(
            format,
            event.getPlayer()));
  }

  private void playPlayerJoinSound(PlayerJoinEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.join.sound.enable") || SyncConnection.ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.join.sound.sound"));
    SoundCategory soundCategory = SoundCategory.valueOf(Amethy.YAMLCONFIG.getString("event.join.sound.soundCategory"));
    float volume = (float) Amethy.YAMLCONFIG.getDouble("event.join.sound.volume");
    float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.join.sound.pitch");

    if (Amethy.YAMLCONFIG.getBoolean("event.join.sound.targetPlayer")) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
      }
    } else {
      for (Player p : Bukkit.getOnlinePlayers()) {
        if (!p.equals(player)) {
          p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
        }
      }
    }
  }

}
