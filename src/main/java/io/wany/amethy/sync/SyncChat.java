package io.wany.amethy.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.Message;
import io.wany.amethy.modules.amethy.Database;
import io.wany.modules.network.MySQLResult;
import net.kyori.adventure.text.Component;

public class SyncChat {

  protected static boolean ENABLED = false;
  private static String CHANNEL = "";
  private static String SERVER = "";

  private static String TABLE;

  private static List<SyncChatMessage> outmessages = new ArrayList<>();
  private static List<SyncChatMessage> inmessages = new ArrayList<>();
  private static HashMap<String, SyncChatMessage> brdmessages = new HashMap<>();

  private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private static final Timer timer = new Timer();

  protected static void onPlayerChat(AsyncChatEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    Component message = event.message();

    SyncChatMessage scm = new SyncChatMessage(
        SERVER,
        player.getName(),
        message,
        System.currentTimeMillis());
    outmessages.add(scm);
  }

  public static void testChat(String message) {
    SyncChatMessage scm = new SyncChatMessage(
        SERVER,
        "TESTMAN",
        message,
        System.currentTimeMillis());
    outmessages.add(scm);
  }

  private static void update() {
    get();
    inmessages.forEach(scm -> {
      if (!brdmessages.containsKey(scm.key)) {
        scm.broadcast();
        brdmessages.put(scm.key, scm);
      }
    });
    outmessages.forEach(scm -> {
      insert(scm);
    });
    inmessages = new ArrayList<>();
    outmessages = new ArrayList<>();
    delete();
  }

  private static void get() {
    try {
      Object[] o = { CHANNEL, SERVER };
      MySQLResult result = Database.query(
          "SELECT server, name, message, datetime FROM " + TABLE + " WHERE `channel` LIKE ? AND `server` NOT LIKE ?",
          o);
      for (int i = 0; i < result.size(); i++) {
        HashMap<String, String> kv = result.get(i);
        SyncChatMessage scm = new SyncChatMessage(
            kv.get("server"),
            kv.get("name"),
            kv.get("message"),
            Long.parseLong(kv.get("datetime")));
        inmessages.add(scm);
      }
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }

  private static void insert(SyncChatMessage scm) {
    try {
      Object[] o = { CHANNEL, SERVER, scm.name, Message.stringify(scm.message), System.currentTimeMillis(), scm.key };
      Database.query("INSERT INTO " + TABLE
          + " (`channel`, `server`, `name`, `message`, `datetime`, `key`) VALUES (?, ?, ?, ?, ?, ?)", o);
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }

  private static void delete() {
    long offset = 10000;
    try {
      Database.query("DELETE FROM " + TABLE + " WHERE `datetime` < " + (System.currentTimeMillis() - offset));
    } catch (Exception e) {
      // e.printStackTrace();
    }
    for (int i = 0; i < brdmessages.keySet().size(); i++) {
      String key = (String) brdmessages.keySet().toArray()[i];
      if (brdmessages.get(key).datetime < (System.currentTimeMillis() - offset)) {
        brdmessages.remove(key);
      }
    }
  }

  protected static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.chat.enable")) {
      Console.debug(Sync.PREFIX + "채팅 동기화 &c비활성화됨");
      return;
    }

    TABLE = Database.TABLE_PREFIX + "sync_chat";
    try {
      MySQLResult result = Database.query("SHOW TABLES LIKE '" + TABLE + "'");
      if (result.getString(0, "TABLE_NAME") == null) {
        Console.log(Sync.PREFIX + "데이터베이스에서 " + TABLE
            + " 테이블을 찾을 수 없습니다. 테이블을 생성합니다.");
        Database.query("CREATE TABLE " + TABLE + " ("
            + "`channel` VARCHAR(128) NOT NULL, "
            + "`server` VARCHAR(128) NOT NULL, "
            + "`name` VARCHAR(128) NOT NULL, "
            + "`message` LONGTEXT NOT NULL, "
            + "`datetime` LONG NOT NULL, "
            + "`key` VARCHAR(128) NOT NULL, "
            + "PRIMARY KEY (`key`) "
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC");
      }
    } catch (Exception e) {
      Console.warn(Sync.PREFIX + "테이블 확인 중 오류가 발생하였습니다. 기능이 비활성화됩니다.");
      e.printStackTrace();
      return;
    }

    CHANNEL = Amethy.CONFIG.getString("sync.chat.channel");
    CHANNEL = CHANNEL.replaceAll("[^a-z0-9_-]", "");
    if (CHANNEL.length() <= 0) {
      Console.warn(Sync.PREFIX + "채팅 동기화 채널 값이 잘못 설정되었거나 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return;
    }
    Console.debug(Sync.PREFIX + "채팅 동기화 채널: " + CHANNEL);

    SERVER = Amethy.CONFIG.getString("server.name");
    if (SERVER.length() <= 0) {
      Console.warn(Sync.PREFIX + "서버 이름 값이 잘못 설정되었거나 확인할 수 없습니다. 채팅 동기화 기능이 비활성화됩니다.");
      return;
    }
    Console.debug(Sync.PREFIX + "채팅 동기화 서버 이름: " + SERVER);

    ENABLED = true;
    Console.debug(Sync.PREFIX + "채팅 동기화 &a활성화됨");

    executorService.submit(() -> {
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          update();
        }
      }, 0, 100);
    });
  }

  protected static void onDisable() {
    if (!ENABLED) {
      return;
    }

    timer.cancel();
    executorService.shutdown();
  }

}
