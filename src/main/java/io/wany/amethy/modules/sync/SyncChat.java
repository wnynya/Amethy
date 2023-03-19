package io.wany.amethy.modules.sync;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethy.modules.MsgUtil;
import io.wany.amethyst.Json;
import net.kyori.adventure.text.Component;

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
    data.set("displayName", MsgUtil.stringify(player.displayName()));
    data.set("message", MsgUtil.stringify(message));

    DatabaseSyncEvent.emit("sync/chat", data);
  }

  private static void databasePlayerChat(DatabaseSyncEvent event) {
    Json data = event.getValue();

    Component component = MsgUtil.formatDatabaseSyncPlayerChat(
        Amethy.YAMLCONFIG.getString("event.chat.msg.sync.format"),
        event.getServer(),
        MsgUtil.parse(data.getString("displayName")),
        MsgUtil.parse(data.getString("message")));

    Bukkit.broadcast(component);

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
