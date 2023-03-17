package io.wany.amethy.modules.sync;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import io.wany.amethy.Amethy;
import io.wany.amethy.Console;
import io.wany.amethy.modules.Message;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethyst.Json;

public class SyncConnection {

  public static boolean ENABLED = false;

  public static void databasePlayerJoin(DatabaseSyncEvent event) {
    Json data = event.getValue();

    // join 메시지
    if (Amethy.YAMLCONFIG.getBoolean("event.join.msg.enable")) {
      Bukkit.broadcast(Message.formatDatabaseSyncPlayerConnection(
          Amethy.YAMLCONFIG.getString("event.join.msg.format"),
          event.getServer(),
          Message.parse(data.getString("displayName"))));
    }

    // join 사운드
    if (Amethy.YAMLCONFIG.getBoolean("event.join.sound.enable")) {
      Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.join.sound.sound"));
      SoundCategory soundCategory = SoundCategory
          .valueOf(Amethy.YAMLCONFIG.getString("event.join.sound.soundCategory"));
      float volume = (float) Amethy.YAMLCONFIG.getDouble("event.join.sound.volume");
      float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.join.sound.pitch");

      for (Player p : Bukkit.getOnlinePlayers()) {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
      }
    }
  }

  public static void databasePlayerQuit(DatabaseSyncEvent event) {
    Json data = event.getValue();

    // quit 메시지
    if (Amethy.YAMLCONFIG.getBoolean("event.quit.msg.enable")) {
      Bukkit.broadcast(Message.formatDatabaseSyncPlayerConnection(
          Amethy.YAMLCONFIG.getString("event.quit.msg.format"),
          event.getServer(),
          Message.parse(data.getString("displayName"))));
    }

    // quit 사운드
    if (Amethy.YAMLCONFIG.getBoolean("event.quit.sound.enable")) {
      Sound sound = Sound.valueOf(Amethy.YAMLCONFIG.getString("event.quit.sound.sound"));
      SoundCategory soundCategory = SoundCategory
          .valueOf(Amethy.YAMLCONFIG.getString("event.quit.sound.soundCategory"));
      float volume = (float) Amethy.YAMLCONFIG.getDouble("event.quit.sound.volume");
      float pitch = (float) Amethy.YAMLCONFIG.getDouble("event.quit.sound.pitch");

      for (Player p : Bukkit.getOnlinePlayers()) {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
      }
    }
  }

  protected static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.connection.enable")) {
      Console.debug(Sync.PREFIX + "연결 동기화 §c비활성화됨");
      return;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      Console.warn(Sync.PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      Console.debug(Sync.PREFIX + "연결 동기화 §c비활성화됨");
      return;
    }

    ENABLED = true;
    Console.debug(Sync.PREFIX + "연결 동기화 §a활성화됨");

    DatabaseSyncEvent.on("sync/connection/join", (args) -> {
      databasePlayerJoin((DatabaseSyncEvent) args[0]);
    });
    DatabaseSyncEvent.on("sync/connection/quit", (args) -> {
      databasePlayerQuit((DatabaseSyncEvent) args[0]);
    });

  }

}
