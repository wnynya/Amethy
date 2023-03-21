package io.wany.amethy.modules.database;

import java.sql.SQLException;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethyst.Json;
import io.wany.amethyst.network.MySQLResult;

public class DatabaseSyncMap {

  public static boolean ENABLED = false;
  private static String TABLE;

  public static void set(String key, Json value) {
    if (!ENABLED) {
      return;
    }

    if (get(key) == null) {
      insert(key, value);
    } else {
      update(key, value);
    }
  }

  public static void set(String key, String value) {
    Json data = new Json();
    data.set("value", value);
    set(key, data);
  }

  public static Json get(String key) {
    if (!ENABLED) {
      return null;
    }

    return select(key);
  }

  public static String getString(String key) {
    if (!ENABLED) {
      return null;
    }
    Json data = select(key);
    if (data == null) {
      return null;
    } else {
      return data.getString("value");
    }
  }

  private static void create() throws SQLException {
    Database.query("CREATE TABLE " + TABLE + " ("
        + "`key` VARCHAR(1024) NOT NULL, "
        + "`value` LONGTEXT NOT NULL, "
        + "`updated` BIGINT NOT NULL, "
        + "PRIMARY KEY(`key`) "
        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC");
  }

  private static void insert(String key, Json value) {
    try {
      Object[] o = { key, value.toString(), System.currentTimeMillis() };
      Database.query("INSERT INTO " + TABLE
          + " (`key`, `value`, `updated`) VALUES (?, ?, ?)", o);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception ignored) {
    }
  }

  private static void update(String key, Json value) {
    try {
      Object[] o = { value.toString(), System.currentTimeMillis(), key };
      Database.query("UPDATE " + TABLE
          + " SET `value` = ?, `updated` = ? WHERE `key` = ?", o);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception ignored) {
    }
  }

  private static Json select(String key) {
    try {
      Object[] o = { key };
      MySQLResult result = Database.query("SELECT * FROM " + TABLE + " WHERE `key` = ?", o);

      String value = result.getString(0, "value");
      if (value == null) {
        return null;
      } else {
        return new Json(value);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception ignored) {
    }
    return null;
  }

  public static void onLoad() {
    if (!Amethy.YAMLCONFIG.getBoolean("database.sync.map.enable")) {
      console.debug(Database.PREFIX + "동기화 맵 §c비활성화됨");
      return;
    }

    TABLE = Database.TABLE_PREFIX + "sync_map";

    try {
      MySQLResult result = Database.query("SHOW TABLES LIKE '" + TABLE + "'");
      if (result.getString(0, "TABLE_NAME") == null) {
        console.log(Database.PREFIX + "데이터베이스에서 " + TABLE
            + " 테이블을 찾을 수 없습니다. 테이블을 생성합니다.");
        create();
      }
    } catch (SQLException e) {
      console.warn(Database.PREFIX + "테이블 확인 중 오류가 발생하였습니다.");
      console.debug(Database.PREFIX + "이벤트 §c비활성화됨");
      e.printStackTrace();
      return;
    }

    ENABLED = true;
    console.debug(Database.PREFIX + "동기화 맵 §a활성화됨");
  }

  public static void onDisable() {
  }

}
