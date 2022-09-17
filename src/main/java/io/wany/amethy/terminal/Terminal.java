package io.wany.amethy.terminal;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Config;
import io.wany.amethy.modules.Request;
import io.wany.amethy.modules.WebSocketClient;

public class Terminal {

  public static String ID;
  private static String PKEY;
  protected static String KEY = "401790cf28f159d50950333f0856e482";
  protected static WebSocketClient WEBSOCKET;
  protected static boolean ISRELOAD = Bukkit.getWorlds().size() != 0;

  public static boolean ping() {
    try {
      JsonObject res = Request.JSONGet(Amethy.API + "/ping", KEY);
      String pong = res.get("message").getAsString();
      if (pong.equals("pong!")) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public static String[] newID() throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    JsonObject res = Request.JSONPost(Amethy.API + "/terminal/nodes", new JsonObject(), KEY);
    JsonObject data = res.get("data").getAsJsonObject();
    String id = data.get("id").getAsString();
    String key = data.get("key").getAsString();
    return new String[] { id, key };
  }

  public static boolean checkID(String id, String key) {
    try {
      Request.JSONGet(Amethy.API + "/terminal/nodes/" + id + "/check?p=" + key, KEY);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static String[] getID() {
    Config a = new Config(".amethy");
    String id = a.getString("id");
    String key = a.getString("key");
    if (id == null || key == null) {
      try {
        String[] d = newID();
        a.set("id", d[0]);
        a.set("key", d[1]);
      } catch (Exception e) {
      }
    }
    return new String[] { id, key };
  }

  private static void loadWebSocket() {
    if (!ping()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ignored) {
      }
      loadWebSocket();
      return;
    }
    String[] d = getID();
    ID = d[0];
    PKEY = d[1];
    if (!checkID(ID, PKEY)) {
      Config a = new Config(".amethy");
      a.set("id", null);
      a.set("key", null);
      String[] d2 = getID();
      ID = d2[0];
      PKEY = d2[1];
    }
    WebSocketClient.Options options = new WebSocketClient.Options();
    options.AUTO_RECONNECT = true;
    options.HEADERS.put("Authorization", KEY);
    options.HEADERS.put("n", ID);
    options.HEADERS.put("p", PKEY);
    try {
      WEBSOCKET = new WebSocketClient(new URI("wss://api.wany.io/amethy/terminal/node"), options);
    } catch (Exception e) {
      e.printStackTrace();
    }
    WEBSOCKET.on("open", (args) -> {
    });
    WEBSOCKET.on("json", (args) -> {
      String event = args[1].toString();
      String message = args[2].toString();
      JsonObject data = (JsonObject) args[3];
      TerminalListener.on(event, message, data);
    });
    WEBSOCKET.on("close", (args) -> {
    });
    WEBSOCKET.on("failed", (args) -> {
      disableWebSocket();
      loadWebSocket();
    });
    try {
      WEBSOCKET.open();
    } catch (Exception ignored) {
    }
  }

  private static void disableWebSocket() {
    WEBSOCKET.close();
    WEBSOCKET.disable();
  }

  public static void event(String event, JsonElement data, String message) {
    if (WEBSOCKET == null) {
      return;
    }
    WEBSOCKET.event(event, data, message);
  }

  public static void event(String event, JsonElement data) {
    if (WEBSOCKET == null) {
      return;
    }
    WEBSOCKET.event(event, data);
  }

  public static void event(String event) {
    if (WEBSOCKET == null) {
      return;
    }
    WEBSOCKET.event(event);
  }

  public static JsonObject grant(String id)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    if (!WEBSOCKET.isConnected()) {
      throw new ConnectException();
    }
    JsonObject data = new JsonObject();
    data.addProperty("account", id);
    return Request.JSONPost(Amethy.API + "/terminal/nodes/" + ID + "/grant", data, KEY);
  }

  public static void onLoad() {
    Executors.newFixedThreadPool(1).submit(() -> {
      loadWebSocket();

      TerminalConsole.onLoad();
    });
  }

  public static void onEnable() {
    Executors.newFixedThreadPool(1).submit(() -> {
      TerminalDashboard.onEnable();
      TerminalPlayers.onEnable();
    });
  }

  public static void onDisable() {
    Executors.newFixedThreadPool(1).submit(() -> {
      TerminalDashboard.onDisable();
      TerminalConsole.onDisable();
      TerminalPlayers.onDisable();

      disableWebSocket();
    });
  }

  public static WebSocketClient getWEBSOCKET() {
    return WEBSOCKET;
  }
}
