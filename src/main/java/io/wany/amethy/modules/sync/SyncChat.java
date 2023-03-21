package io.wany.amethy.modules.sync;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.console;
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

  protected static boolean ENABLED = false;

  protected static void onPlayerChat(AsyncChatEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    Component message = event.message();

    Json data = new Json();
    data.set("uuid", player.getUniqueId().toString());
    data.set("message", Amethy.MESSAGE.stringify(message));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  protected static void onPlayerChat(AsyncPlayerChatEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();

    Json data = new Json();
    data.set("uuid", player.getUniqueId().toString());
    data.set("message", Amethy.MESSAGE.stringify(event.getMessage()));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  private static void databasePlayerChat(DatabaseSyncEvent event) {
    Json data = event.getValue();

    String format = Amethy.YAMLCONFIG.getString("event.chat.msg.sync.format");
    String server = event.getServer();
    UUID uuid = UUID.fromString(data.getString("uuid"));
    String message = data.getString("message");

    if (Amethy.PAPERAPI) {
      Bukkit.broadcast(PaperMessage.Formatter.PLAYER_CHAT_SERVER.format(format, server, uuid, message));
    }
    else {
      Bukkit.broadcastMessage(SpigotMessage.Formatter.PLAYER_CHAT_SERVER.format(format, server, uuid, Amethy.MESSAGE.stringify(message)));
    }

    if (Amethy.YAMLCONFIG.getBoolean("event.chat.sound.enable")) {
      Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.sound"));
      SoundCategory soundCategory = SoundCategory.valueOf(Amethy.YAMLCONFIG.getString("event.chat.sound.soundCategory"));
      float volume = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.volume");
      float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.chat.sound.pitch");

      Bukkit.getOnlinePlayers().forEach((p) -> {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
      });
    }
  }

  protected static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.chat.enable")) {
      console.debug(Sync.PREFIX + "채팅 동기화 §c비활성화됨");
      return;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      console.warn(Sync.PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(Sync.PREFIX + "채팅 동기화 §c비활성화됨");
      return;
    }

    ENABLED = true;
    console.debug(Sync.PREFIX + "채팅 동기화 §a활성화됨");

    DatabaseSyncEvent.on("sync/chat", (args) -> {
      databasePlayerChat((DatabaseSyncEvent) args[0]);
    });
  }

}
