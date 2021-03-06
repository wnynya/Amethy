package io.wany.amethy.modules;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Builder;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebSocketClient extends EventEmitter {

  public static String USER_AGENT = "Amethy";

  private final URI uri;
  private final Options options;

  private WebSocket connection = null;
  private boolean connected = false;
  private boolean closed = true;

  private final ExecutorService pingExecutorService = Executors.newFixedThreadPool(1);
  private final Timer pingTimer = new Timer();

  private final ExecutorService reconnectExecutorService = Executors.newFixedThreadPool(1);
  private final Timer reconnectTimer = new Timer();

  public int openFailed = 0;

  public WebSocketClient(URI uri, Options options) {
    super();

    this.uri = uri;
    this.options = options;

    if (!this.options.HEADERS.containsKey("User-Agent")) {
      this.options.HEADERS.replace("User-Agent", USER_AGENT);
    }

    pingExecutorService.submit(() -> pingTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (closed || connected || connection != null) {
          return;
        }
        try {
          connection.sendPing(ByteBuffer.allocate(0));
        } catch (Exception ignored) {
        }
      }
    }, 3000, 1000 * 30));

    reconnectExecutorService.submit(() -> reconnectTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (!options.AUTO_RECONNECT || closed || connected || connection != null) {
          return;
        }
        try {
          open();
        } catch (Exception e) {
          openFailed++;
          if (openFailed >= 3) {
            emit("failed", "");
            openFailed = 0;
            close();
          }
        }
      }
    }, 3000, 1000));
  }

  public void open() {
    if (this.connection != null || this.connected) {
      return;
    }
    this.closed = false;

    Builder builder = HttpClient.newHttpClient().newWebSocketBuilder();
    this.options.HEADERS.forEach((key, value) -> {
      builder.header(key, value);
    });
    builder.connectTimeout(Duration.ofMillis(1000));
    this.connection = builder.buildAsync(this.uri, new WebSocketListener(this)).join();
  }

  private class WebSocketListener implements WebSocket.Listener {

    private final WebSocketClient client;
    private StringBuilder message = new StringBuilder();

    public WebSocketListener(WebSocketClient client) {
      this.client = client;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
      client.connected = true;
      client.connection = webSocket;
      openFailed = 0;
      client.emit("open", client);
      WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence chs, boolean last) {
      if (last) {
        message.append(chs);
        String string = message.toString();
        client.emit("message", webSocket, string);
        try {
          JsonObject object = JsonParser.parseString(string).getAsJsonObject();
          String event = object.get("event").getAsString();
          String message = object.get("message").getAsString();
          JsonObject data = object.get("data").getAsJsonObject();
          client.emit("json", client, event, message, data);
          client.emit("text", client, string);
        } catch (Exception e) {
          client.emit("text", client, string);
        }
        message = new StringBuilder();
      } else {
        message.append(chs);
      }
      return WebSocket.Listener.super.onText(webSocket, chs, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
      client.connected = false;
      client.connection.sendClose(0, "");
      client.connection.abort();
      client.connection = null;
      client.emit("close", client, statusCode, reason);
      return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      client.connected = false;
      client.connection.sendClose(0, "");
      client.connection.abort();
      client.connection = null;
      client.emit("close", client, error);
      WebSocket.Listener.super.onError(webSocket, error);
    }

  }

  public void close() {
    this.closed = true;
    if (this.connection == null) {
      return;
    }
    this.connection.sendClose(0, "");
    this.connection.abort();
    this.connection = null;
  }

  public void send(Object object) {
    if (!this.isConnected()) {
      return;
    }
    String string = null;
    if (object instanceof JsonObject) {
      string = object.toString();
    } else {
      string = object.toString();
    }
    try {
      this.connection.sendText(string, true);
    } catch (Exception ignored) {
    }
  }

  public void event(String event, JsonElement data, String message) {
    JsonObject object = new JsonObject();
    object.addProperty("event", event);
    object.add("data", data);
    object.addProperty("message", message);
    this.send(object);
  }

  public void event(String event, JsonElement data) {
    event(event, data, "");
  }

  public void event(String event) {
    event(event, new JsonObject(), "");
  }

  public boolean isConnected() {
    return connected;
  }

  public boolean isClosed() {
    return closed;
  }

  public void disable() {
    this.reconnectTimer.cancel();
    this.reconnectExecutorService.shutdown();
  }

  public static class Options {
    public boolean AUTO_RECONNECT;
    public HashMap<String, String> HEADERS;

    public Options() {
      this.AUTO_RECONNECT = false;
      this.HEADERS = new HashMap<>();
    }
  }
}
