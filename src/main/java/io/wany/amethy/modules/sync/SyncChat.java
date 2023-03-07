package io.wany.amethy.modules.sync;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.Console;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethy.modulesmc.Message;
import io.wany.amethyst.Json;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

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
    data.set("name", player.getName());
    data.set("displayName", GsonComponentSerializer.gson().serialize(player.displayName()));
    data.set("message", Message.stringify(message));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  private static void databasePlayerChat(DatabaseSyncEvent event) {
    Json data = event.getValue();
    Bukkit.broadcast(Message.of(
        "[" + event.getServer() + "] ",
        GsonComponentSerializer.gson().deserialize(data.getString("displayName")),
        ": ",
        data.getString("message")));
  }

  protected static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.chat.enable")) {
      Console.debug(Sync.PREFIX + "채팅 동기화 §c비활성화됨");
      return;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      Console.warn(Sync.PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      Console.debug(Sync.PREFIX + "채팅 동기화 §c비활성화됨");
      return;
    }

    ENABLED = true;
    Console.debug(Sync.PREFIX + "채팅 동기화 §a활성화됨");

    DatabaseSyncEvent.on("sync/chat", (args) -> {
      databasePlayerChat((DatabaseSyncEvent) args[0]);
    });
  }

}
