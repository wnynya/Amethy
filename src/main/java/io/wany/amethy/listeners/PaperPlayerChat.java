package io.wany.amethy.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.PaperMessage;
import io.wany.amethy.modules.sync.Sync;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaperPlayerChat implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(AsyncChatEvent event) {
    if (event.isCancelled()) {
      return;
    }

    setPlayerChatRenderer(event);
    playPlayerChatSound(event);
    Sync.onPlayerChat(event);
  }

  private static void setPlayerChatRenderer(AsyncChatEvent event) {
    if (!Amethy.YAMLCONFIG.getBoolean("event.chat.msg.normal.enable")) {
      return;
    }

    Component component = PaperMessage.Formatter.PLAYER_CHAT
        .format(Amethy.YAMLCONFIG.getString("event.chat.msg.normal.format"), event.getPlayer(), event.message());

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
