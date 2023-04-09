package io.wany.amethy.modules.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethyst.EventEmitter;
import io.wany.amethyst.Json;
import io.wany.amethyst.network.MySQLResult;

public class DatabaseSyncEvent {

  private static final EventEmitter eventEmitter = new EventEmitter();
  private static final HashMap<Long, String> processedEvents = new HashMap<>();

  private static ExecutorService onLoadExecutor = Executors.newFixedThreadPool(1);
  private static Timer onLoadTimer100m = new Timer();
  private static Timer onLoadTimer1s = new Timer();

  public static boolean ENABLED = false;
  private static String TABLE;

  private String server;
  private String event;
  private Json value;
  private long emitted;

  private DatabaseSyncEvent(String event, Json value) {
    this.server = Database.SERVER;
    this.event = event;
    this.value = value;
    this.emitted = System.currentTimeMillis();
  }

  private DatabaseSyncEvent(String server, String event, Json value, long emitted) {
    this.server = server;
    this.event = event;
    this.value = value;
    this.emitted = emitted;
  }

  public String getServer() {
    return this.server;
  }

  public String getEvent() {
    return this.event;
  }

  public Json getValue() {
    return this.value;
  }

  public static void on(String event, Consumer<Object[]> consumer) {
    eventEmitter.on(event, consumer);
    console.debug(Database.PREFIX + "이벤트 리스너 등록됨: " + event);
  }

  public static void emit(String event, Json value) {
    if (!ENABLED) {
      return;
    }
    insert(new DatabaseSyncEvent(event, value));
  }

  public static boolean isProcessed(long emitted, String key) {
    String keyg = processedEvents.get(emitted);
    return key.equals(keyg);
  }

  private static void process(DatabaseSyncEvent event) {
    String key = event.emitted + "$" + event.event + "$" + event.value;
    if (!isProcessed(event.emitted, key)) {
      processedEvents.put(event.emitted, key);
      eventEmitter.emit(event.event, event);
    }
    processedEvents.keySet().removeIf((emitted) -> {
      return emitted < System.currentTimeMillis() - 5000;
    });
  }

  private static void create() throws SQLException {
    Database.query("CREATE TABLE " + TABLE + " ("
        + "`server` VARCHAR(128) NOT NULL, "
        + "`event` VARCHAR(128) NOT NULL, "
        + "`value` LONGTEXT NOT NULL, "
        + "`emitted` BIGINT NOT NULL "
        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC");
  }

  private static void insert(DatabaseSyncEvent event) {
    try {
      Object[] o = { event.server, event.event, event.value.toString(), event.emitted };
      Database.query("INSERT INTO " + TABLE
          + " (`server`, `event`, `value`, `emitted`) VALUES (?, ?, ?, ?)", o);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
    }
  }

  private static void select() {
    try {
      MySQLResult result = Database.query("SELECT * FROM " + TABLE
          + " WHERE `server` NOT LIKE '" + Database.SERVER + "'"
          + " AND `emitted` > " + (System.currentTimeMillis() - 3000)
          + " ORDER BY `emitted` ASC");

      for (int i = 0; i < result.size(); i++) {
        process(new DatabaseSyncEvent(
            result.getString(i, "server"),
            result.getString(i, "event"),
            new Json(result.getString(i, "value")),
            result.getLong(i, "emitted")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
    }
  }

  private static void delete() {
    try {
      Object[] o = { System.currentTimeMillis() - 5000 };
      Database.query("DELETE FROM " + TABLE
          + " WHERE `emitted` < ?", o);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
    }
  }

  public static void onLoad() {
    if (!Amethy.YAMLCONFIG.getBoolean("database.sync.event.enable")) {
      console.debug(Database.PREFIX + "동기화 이벤트 §c비활성화됨");
      return;
    }

    TABLE = Database.TABLE_PREFIX + "sync_event";

    try {
      MySQLResult result = Database.query("SHOW TABLES LIKE '" + TABLE + "'");
      if (result.getString(0, "TABLE_NAME") == null) {
        console.log(Database.PREFIX + "데이터베이스에서 " + TABLE
            + " 테이블을 찾을 수 없습니다. 테이블을 생성합니다.");
        create();
      }
    } catch (SQLException e) {
      console.warn(Database.PREFIX + "테이블 확인 중 오류가 발생하였습니다.");
      console.debug(Database.PREFIX + "동기화 이벤트 §c비활성화됨");
      e.printStackTrace();
      return;
    }

    ENABLED = true;
    console.debug(Database.PREFIX + "동기화 이벤트 §a활성화됨");

    onLoadExecutor.submit(() -> {
      onLoadTimer100m.schedule(new TimerTask() {
        @Override
        public void run() {
          select();
        }
      }, 0, 100);
      onLoadTimer1s.schedule(new TimerTask() {
        @Override
        public void run() {
          delete();
        }
      }, 0, 1000);
    });
  }

  public static void onDisable() {
    onLoadTimer100m.cancel();
    onLoadTimer1s.cancel();
    onLoadExecutor.shutdownNow();
  }

}
