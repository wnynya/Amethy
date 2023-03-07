package io.wany.amethy.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modulesmc.Message;
import net.kyori.adventure.text.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerChat implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(AsyncChatEvent event) {
    setPlayerChatRenderer(event);
    playPlayerChatSound(event);
    Sync.onPlayerChat(event);
  }

  private static void setPlayerChatRenderer(AsyncChatEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.chat.msg.normal.chat.enable")) {
      return;
    }

    Component component = Message.formatPlayerChat(event.getPlayer(), event.message(),
        Amethy.YAMLCONFIG.getString("event.chat.msg.normal.chat.format"));
    ExecutorService e = Executors.newSingleThreadExecutor();
    e.submit(() -> {
      Bukkit.broadcast(component);
      e.shutdown();
    });
    event.setCancelled(true);
  }

  private static void playPlayerChatSound(AsyncChatEvent event) {
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
