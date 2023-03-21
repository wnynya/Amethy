package io.wany.amethy.listeners;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.SpigotMessage;
import io.wany.amethy.modules.sync.Sync;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpigotPlayerChat implements Listener {

  @EventHandler
  public void onEvent(AsyncPlayerChatEvent event) {
    setPlayerChatFormat(event);
    playPlayerChatSound(event);
    Sync.onPlayerChat(event);
  }

  private static void setPlayerChatFormat(AsyncPlayerChatEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.chat.msg.normal.enable")) {
      return;
    }

    String message = SpigotMessage.Formatter.PLAYER_CHAT.format(Amethy.YAMLCONFIG.getString("event.chat.msg.normal.format"), event.getPlayer(), event.getMessage());

    ExecutorService e = Executors.newSingleThreadExecutor();
    e.submit(() -> {
      Bukkit.broadcastMessage(message);
      e.shutdown();
    });

    event.setCancelled(true);
  }

  private static void playPlayerChatSound(AsyncPlayerChatEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.chat.sound.enable")) {
      return;
    }

    Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.sound"));
    SoundCategory soundCategory = SoundCategory.valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.soundCategory"));
    float volume = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.volume");
    float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.pitch");

    Bukkit.getOnlinePlayers().forEach((p) -> {
      p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
    });
  }

}
