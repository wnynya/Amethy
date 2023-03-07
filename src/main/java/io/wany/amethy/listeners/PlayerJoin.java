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
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modules.wand.Wand;
import io.wany.amethy.modulesmc.Console;
import io.wany.amethy.modulesmc.Message;
import io.wany.amethy.supports.cucumbery.CucumberySupport;

import java.util.HashMap;

public class PlayerJoin implements Listener {

  public static HashMap<String, String> changeJoinPlayers = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    CucumberySupport.onPlayerJoin(event);
    chatPlayerJoinMessage(event);
    consolePlayerJoinMessage(event);
    playPlayerJoinSound(event);
    updatePlayerJoin(event);
    Sync.onPlayerJoin(event);
    Wand.onPlayerJoin(event);
    BungeeTeleportCommand.onPlayerJoin(event);
  }

  private void chatPlayerJoinMessage(PlayerJoinEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.join.msg.normal.chat.enable")) {
      return;
    }
    Player player = event.getPlayer();
    String format = Amethy.YAMLCONFIG.getString("event.join.msg.normal.chat.format");
    if (changeJoinPlayers.containsKey(player.getName())
        && Amethy.YAMLCONFIG.getBoolean("event.join.msg.change.chat.enable")) {
      format = Amethy.YAMLCONFIG.getString("event.join.msg.change.chat.format");
      format = format.replace("{fromserver}", changeJoinPlayers.get(player.getName()));
      changeJoinPlayers.remove(player.getName());
    }
    format = Message.effect(format);
    event.joinMessage(Message.formatPlayer(player, format));
  }

  private void consolePlayerJoinMessage(PlayerJoinEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.join.msg.normal.console.enable")) {
      return;
    }
    Player player = event.getPlayer();
    String format = Amethy.YAMLCONFIG.getString("event.join.msg.normal.console.format");
    format = Message.effect(format);
    Console.log(Message.stringify(Message.formatPlayer(player, format)));
  }

  private void playPlayerJoinSound(PlayerJoinEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.join.sound.enable")) {
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

  private void updatePlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (Amethy.YAMLCONFIG.getBoolean("event.join.list.enable")) {
      String format = Amethy.YAMLCONFIG.getString("event.join.list.format");
      format = Message.effect(format);
      player.playerListName(Message.formatPlayer(player, format));
    }
  }

}
