package io.wany.amethy.modules.database;

import java.sql.SQLException;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethyst.network.MySQLClient;
import io.wany.amethyst.network.MySQLConfig;
import io.wany.amethyst.network.MySQLResult;

public class Database {

  public static String PREFIX = "§l[데이터베이스]:§r ";
  public static boolean ENABLED = false;

  private static MySQLClient client;
  public static String TABLE_PREFIX = "";
  public static String SERVER = "";

  public static MySQLResult query(String q) throws SQLException {
    if (!ENABLED) {
      return null;
    }
    return client.query(q);
  }

  public static MySQLResult query(String q, Object[] o) throws SQLException {
    if (!ENABLED) {
      return null;
    }
    return client.query(q, o);
  }

  public static void onLoad() {
    if (!Amethy.YAMLCONFIG.getBoolean("database.enable")) {
      console.debug(PREFIX + "데이터베이스 연결 §c비활성화됨");
      return;
    }

    SERVER = Amethy.YAMLCONFIG.getString("server.name");
    if (SERVER.length() <= 0) {
      console.warn(PREFIX + "서버 이름 값이 잘못 설정되었거나 확인할 수 없습니다. 데이터베이스 기능이 §c비활성화§r됩니다.");
      return;
    }
    console.debug(PREFIX + "서버 이름: " + SERVER);

    console.debug(PREFIX + "데이터베이스 연결 §a활성화됨");
    TABLE_PREFIX = Amethy.YAMLCONFIG.getString("database.mysql.tableprefix");
    try {
      console.debug(PREFIX + "데이터베이스 연결 중...");
      MySQLConfig cfg = new MySQLConfig(
          Amethy.YAMLCONFIG.getString("database.mysql.host"),
          Amethy.YAMLCONFIG.getInt("database.mysql.port"),
          Amethy.YAMLCONFIG.getString("database.mysql.username"),
          Amethy.YAMLCONFIG.getString("database.mysql.password"),
          Amethy.YAMLCONFIG.getString("database.mysql.database"))
          .autoReconnect();
      client = new MySQLClient("amethy", cfg);
      ENABLED = true;
      console.debug(PREFIX + "데이터베이스 연결됨");
    } catch (SQLException e) {
      console.warn(PREFIX + "데이터베이스 연결 실패");
      e.printStackTrace();
      console.debug(PREFIX + "데이터베이스 연결 §c비활성화됨");
      return;
    }

    DatabaseSyncMap.onLoad();
    DatabaseSyncEvent.onLoad();
  }

  public static void onDisable() {
    if (!ENABLED) {
      return;
    }

    DatabaseSyncMap.onDisable();
    DatabaseSyncEvent.onDisable();

    try {
      client.close();
      console.debug(PREFIX + "데이터베이스 연결 종료");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
