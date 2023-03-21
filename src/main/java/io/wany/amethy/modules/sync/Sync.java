package io.wany.amethy.modules.sync;

import io.wany.amethy.console;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.database.Database;

public class Sync {

  protected static String PREFIX = "§l[동기화]:§r ";
  protected static boolean ENABLED = false;

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }
    SyncPlayer.onPlayerJoin(event);
    SyncVaultEconomy.onPlayerJoin(event);
  }

  public static void onPlayerQuit(PlayerQuitEvent event) {
    if (!ENABLED) {
      return;
    }
    SyncPlayer.onPlayerQuit(event);
  }

  public static void onPlayerChat(AsyncChatEvent event) {
    if (!ENABLED) {
      return;
    }
    SyncChat.onPlayerChat(event);
  }

  public static void onPlayerChat(AsyncPlayerChatEvent event) {
    if (!ENABLED) {
      return;
    }
    SyncChat.onPlayerChat(event);
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.enable")) {
      console.debug(PREFIX + "동기화 &c비활성화됨");
      return;
    }

    if (!Database.ENABLED) {
      console.warn(PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(PREFIX + "동기화 &c비활성화됨");
      return;
    }

    ENABLED = true;
    console.debug(PREFIX + "동기화 &a활성화됨");

    SyncPlayer.onEnable();
    SyncChat.onEnable();
    SyncVaultEconomy.onEnable();
    SyncConnection.onEnable();

  }

  public static void onDisable() {
    SyncPlayer.onDisable();
  }

}
