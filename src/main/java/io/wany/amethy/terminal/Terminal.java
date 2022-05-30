package io.wany.amethy.terminal;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
import io.wany.amethy.Config;
import io.wany.amethy.modules.Request;
import io.wany.amethy.modules.WebSocketClient;

public class Terminal {

  public static String ID;
  private static String KEY = "401790cf28f159d50950333f0856e482";
  protected static WebSocketClient WEBSOCKET;

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

  public static String newID() throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    JsonObject res = Request.JSONPost(Amethy.API + "/terminal/nodes", new JsonObject(), KEY);
    String id = res.get("data").getAsString();
    return id;
  }

  public static boolean checkID(String id) {
    try {
      Request.JSONGet(Amethy.API + "/terminal/nodes/" + id + "/check", KEY);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static String getID() {
    Config a = new Config(".amethy");
    String id = a.getString("id");
    if (id == null) {
      try {
        id = newID();
        a.set("id", id);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return id;
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
    ID = getID();
    if (!checkID(ID)) {
      Config a = new Config(".amethy");
      a.set("id", null);
      ID = getID();
    }
    WebSocketClient.Options options = new WebSocketClient.Options();
    options.AUTO_RECONNECT = true;
    options.HEADERS.put("Authorization", KEY);
    options.HEADERS.put("n", ID);
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

  public static void event(String event, JsonObject data, String message) {
    if (WEBSOCKET == null) {
      return;
    }
    WEBSOCKET.event(event, data, message);
  }

  public static void event(String event, JsonObject data) {
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
    });
  }

  public static void onDisable() {
    Executors.newFixedThreadPool(1).submit(() -> {
      TerminalDashboard.onDisable();
      TerminalConsole.onDisable();

      disableWebSocket();
    });
  }

  public static WebSocketClient getWEBSOCKET() {
    return WEBSOCKET;
  }
}
