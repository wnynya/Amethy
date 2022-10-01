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
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.Message;
import io.wany.amethy.modules.PlayerSync;
import io.wany.amethy.sync.Sync;
import io.wany.amethy.wand.Wand;

import java.util.HashMap;

public class PlayerQuit implements Listener {

  public static HashMap<String, String> changeQuitPlayers = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    chatPlayerQuitMessage(event);
    consolePlayerQuitMessage(event);
    playPlayerQuitSound(event);
    chatPlayerChangeQuitMessage(event);
    consolePlayerChangeQuitMessage(event);
    Wand.onPlayerQuit(event);
    Sync.onPlayerQuit(event);
  }

  private void chatPlayerQuitMessage(PlayerQuitEvent event) {
    if (!Amethy.CONFIG.getBoolean("event.quit.msg.normal.chat.enable")) {
      return;
    }
    Player player = event.getPlayer();
    String format = Amethy.CONFIG.getString("event.quit.msg.normal.chat.format");
    if (changeQuitPlayers.containsKey(player.getName())
        && Amethy.CONFIG.getBoolean("event.quit.msg.change.chat.enable")) {
      format = Amethy.CONFIG.getString("event.quit.msg.change.chat.format");
      format = format.replace("{gotoserver}", changeQuitPlayers.get(player.getName()));
      changeQuitPlayers.remove(player.getName());
    }
    format = Message.effect(format);
    event.quitMessage(Message.formatPlayer(player, format));
  }

  private void consolePlayerQuitMessage(PlayerQuitEvent event) {
    if (!Amethy.CONFIG.getBoolean("event.quit.msg.normal.console.enable")) {
      return;
    }
    Player player = event.getPlayer();
    String format = Amethy.CONFIG.getString("event.quit.msg.normal.console.format");
    format = Message.effect(format);
    Console.log(Message.stringify(Message.formatPlayer(player, format)));
  }

  private void playPlayerQuitSound(PlayerQuitEvent event) {
    if (!Amethy.CONFIG.getBoolean("event.quit.sound.enable")) {
      return;
    }
    Sound sound = Sound.valueOf(Amethy.CONFIG.getString("event.quit.sound.sound"));
    SoundCategory soundCategory = SoundCategory.valueOf(Amethy.CONFIG.getString("event.quit.sound.soundCategory"));
    float volume = (float) Amethy.CONFIG.getDouble("event.quit.sound.volume");
    float pitch = (float) Amethy.CONFIG.getDouble("event.quit.sound.pitch");

    for (Player p : Bukkit.getOnlinePlayers()) {
      p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
    }
  }

  private void chatPlayerChangeQuitMessage(PlayerQuitEvent event) {

  }

  private void consolePlayerChangeQuitMessage(PlayerQuitEvent event) {

  }

}
