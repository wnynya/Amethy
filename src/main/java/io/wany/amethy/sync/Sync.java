package io.wany.amethy.sync;

import java.sql.SQLException;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.Crypto;
import io.wany.amethy.modules.amethy.Database;
import io.wany.amethy.modules.network.MySQLResult;

public class Sync {

  protected static String PREFIX = "&l[동기화]:&r ";
  protected static boolean ENABLED = false;

  private static String TABLE;

  protected static String get(String namespace, String key) {
    if (!ENABLED) {
      return null;
    }
    String hash = new Crypto(namespace + '/' + key).hash();
    try {
      MySQLResult result = Database.query("SELECT value FROM " + TABLE
          + " WHERE `key` = '" + hash + "'");
      return result.getString(0, "value");
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  protected static void set(String namespace, String key, String value) {
    if (!ENABLED) {
      return;
    }
    String hash = new Crypto(namespace + '/' + key).hash();
    if (get(namespace, key) == null) {
      try {
        Object[] o = { namespace, hash, value };
        Database.query("INSERT INTO " + TABLE + " (`namespace`, `key`, `value`) VALUES (?, ?, ?)", o);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        Object[] o = { value, hash };
        Database.query("UPDATE " + TABLE + " SET `value` = ? WHERE `key` = ?", o);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

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
    SyncVaultEconomy.onPlayerQuit(event);
  }

  public static void onPlayerChat(AsyncChatEvent event) {
    if (!ENABLED) {
      return;
    }
    SyncChat.onPlayerChat(event);
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.enable")) {
      Console.debug(PREFIX + "동기화 &c비활성화됨");
      return;
    }

    if (!Database.ENABLED) {
      Console.warn(PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return;
    }

    TABLE = Database.TABLE_PREFIX + "sync";
    try {
      MySQLResult result = Database.query("SHOW TABLES LIKE '" + TABLE + "'");
      if (result.getString(0, "TABLE_NAME") == null) {
        Console.log(PREFIX + "데이터베이스에서 " + TABLE
            + " 테이블을 찾을 수 없습니다. 테이블을 생성합니다.");
        Database.query("CREATE TABLE " + TABLE + " ("
            + "`namespace` VARCHAR(128) NOT NULL, "
            + "`key` VARCHAR(128) NOT NULL, "
            + "`value` LONGTEXT NOT NULL, "
            + "PRIMARY KEY (`key`) "
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC");
      }
    } catch (Exception e) {
      Console.warn(PREFIX + "테이블 확인 중 오류가 발생하였습니다. 기능이 비활성화됩니다.");
      e.printStackTrace();
      return;
    }

    ENABLED = true;
    Console.debug(PREFIX + "동기화 &a활성화됨");

    SyncPlayer.onEnable();
    SyncVaultEconomy.onEnable();
    SyncChat.onEnable();

  }

  public static void onDisable() {
    SyncChat.onDisable();
  }

}
