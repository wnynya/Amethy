package io.wany.amethy.modules.amethy;

import java.sql.SQLException;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.network.MySQLClient;
import io.wany.amethy.modules.network.MySQLConfig;
import io.wany.amethy.modules.network.MySQLResult;

public class Database {

  public static String PREFIX = "&l[데이터베이스]:&r ";
  public static boolean ENABLED = false;

  private static MySQLClient client;
  public static String TABLE_PREFIX = "";

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
    if (!Amethy.CONFIG.getBoolean("database.enable")) {
      Console.debug(PREFIX + "데이터베이스 연결 &c비활성화됨");
      return;
    }
    Console.debug(PREFIX + "데이터베이스 연결 &a활성화됨");
    TABLE_PREFIX = Amethy.CONFIG.getString("database.mysql.tableprefix");
    try {
      Console.debug(PREFIX + "데이터베이스 연결 중...");
      MySQLConfig cfg = new MySQLConfig(
          Amethy.CONFIG.getString("database.mysql.host"),
          Amethy.CONFIG.getInt("database.mysql.port"),
          Amethy.CONFIG.getString("database.mysql.username"),
          Amethy.CONFIG.getString("database.mysql.password"),
          Amethy.CONFIG.getString("database.mysql.database"));
      client = new MySQLClient("amethy", cfg);
      ENABLED = true;
      Console.debug(PREFIX + "데이터베이스 연결됨");
    } catch (SQLException e) {
      Console.warn(PREFIX + "데이터베이스 연결 실패");
      e.printStackTrace();
    }
  }

  public static void onDisable() {
    if (!ENABLED) {
      return;
    }
    try {
      client.close();
      Console.debug(PREFIX + "데이터베이스 연결 종료");
    } catch (SQLException e) {
    }
  }

}
