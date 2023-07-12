package io.wany.amethy.modules.sync;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.PaperMessage;
import io.wany.amethy.modules.SpigotMessage;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethyst.Json;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class SyncChat {

  private boolean enabled = false;

  public boolean isEnabled() {
    return this.enabled;
  }

  protected void onEnable() {
    this.enabled = true;

    DatabaseSyncEvent.on("sync/chat", (args) -> {
      onDatabasePlayerChat((DatabaseSyncEvent) args[0]);
    });
  }

  protected void onDisable() {
    if (!this.enabled) {
      return;
    }

    enabled = false;
  }

  protected void onPlayerChat(AsyncChatEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    Component message = event.message();

    Json data = new Json();
    data.set("uuid", player.getUniqueId().toString());
    data.set("message", Amethy.MESSAGE.stringify(message));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  @SuppressWarnings("deprecation") // Spigot API
  protected void onPlayerChat(AsyncPlayerChatEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();

    Json data = new Json();
    data.set("uuid", player.getUniqueId().toString());
    data.set("message", Amethy.MESSAGE.stringify(event.getMessage()));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  @SuppressWarnings("deprecation") // Spigot API
  private void onDatabasePlayerChat(DatabaseSyncEvent event) {
    Json data = event.getValue();

    String format = Amethy.YAMLCONFIG.getString("event.chat.msg.sync.format");
    String server = event.getServer();
    UUID uuid = UUID.fromString(data.getString("uuid"));
    String message = data.getString("message");

    if (Amethy.PAPERAPI) {
      Bukkit.broadcast(
          PaperMessage.Formatter.PLAYER_CHAT_SERVER.format(format, server, uuid, Amethy.MESSAGE.parse(message)));
    } else {
      Bukkit.broadcastMessage(
          SpigotMessage.Formatter.PLAYER_CHAT_SERVER.format(format, server, uuid, Amethy.MESSAGE.parse(message)));
    }

    if (Amethy.YAMLCONFIG.getBoolean("event.chat.sound.enable")) {
      Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.sound"));
      SoundCategory soundCategory = SoundCategory
          .valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.soundCategory"));
      float volume = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.volume");
      float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.pitch");

      Bukkit.getOnlinePlayers().forEach((p) -> {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
      });
    }
  }

}
