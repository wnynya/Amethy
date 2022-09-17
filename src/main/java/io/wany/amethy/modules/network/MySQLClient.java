package io.wany.amethy.modules.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class MySQLClient {

  private static HashMap<String, MySQLClient> databases = new HashMap<>();

  private final String id;
  private Connection conn;

  public MySQLClient(String id, MySQLConfig cfg) throws SQLException {
    this.id = id;
    this.conn = DriverManager.getConnection(cfg.url(), cfg.user(), cfg.password());
    databases.put(this.id, this);
  }

  private Statement getStatement() throws SQLException {
    return this.conn.createStatement();
  }

  public MySQLResult query(String q) throws SQLException {
    Statement s = this.getStatement();
    ResultSet rs = s.executeQuery(q);
    MySQLResult r = new MySQLResult();
    while (rs.next()) {
      ResultSetMetaData meta = rs.getMetaData();
      for (int i = 1; i <= meta.getColumnCount(); i++) {
        r.set(meta.getColumnName(i), rs.getObject(i).toString());
      }
      r.nextIndex();
    }
    r.close();
    rs.close();
    s.close();
    return r;
  }

  public static Collection<MySQLClient> getDatabases() {
    return databases.values();
  }

}
