package io.wany.amethy.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerChat implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(AsyncChatEvent event) {
    setPlayerChatRenderer(event);
    playPlayerChatSound(event);
  }

  private static void setPlayerChatRenderer(AsyncChatEvent event) {
    if (!Amethy.CONFIG.getBoolean("event.chat.msg.normal.chat.enable")) {
      return;
    }
    String chatFormat = Amethy.CONFIG.getString("event.chat.msg.normal.chat.format");
    ChatRenderer chatRenderer = (source, sourceDisplayName, message, viewer) -> {
      String format = chatFormat;
      format = Message.effect(format);
      //message = Message.parse(Message.effect(Message.stringify(message)));
      Component component = Message.formatPlayerChat(source, message, format);
      /*format = Message.formatPlayer(source, format);
      String messageString = Message.stringify(message);
      if (Cherry.CONFIG.getBoolean("event.chat.effect.enable") && source.hasPermission("cherry.event.chat.effect")) {
        messageString = Message.effect(messageString);
      }
      format = format.replace("{message}", messageString);
      format = format.replace("{msg}", messageString);*/
      return component;
    };
    event.renderer(chatRenderer);
  }

  private static void playPlayerChatSound(AsyncChatEvent event) {
    if (!Amethy.CONFIG.getBoolean("event.chat.sound.enable")) {
      return;
    }
    Sound sound = Sound.valueOf(Amethy.CONFIG.getString("event.chat.sound.sound"));
    SoundCategory soundCategory = SoundCategory.valueOf(Amethy.CONFIG.getString("event.chat.sound.soundCategory"));
    float volume = (float) Amethy.CONFIG.getDouble("event.chat.sound.volume");
    float pitch = (float) Amethy.CONFIG.getDouble("event.chat.sound.pitch");

    for (Player p : Bukkit.getOnlinePlayers()) {
      p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
    }
  }

}
