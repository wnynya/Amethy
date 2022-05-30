package io.wany.amethy.terminal;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class TerminalConsole {

  public static TerminalConsoleLogFilter logFilter;
  public static List<Log> offlineLogs = new ArrayList<>();

  public static class Log {
    public final String message;
    public final long time;
    public final String level;
    public final String thread;
    public final String logger;

    public Log(String message, long time, String level, String thread, String logger) {
      this.message = message;
      this.time = time;
      this.level = level;
      this.thread = thread;
      this.logger = logger;
    }
  }

  public static void sendLog(Log log) {
    JsonObject data = new JsonObject();
    data.addProperty("message", log.message);
    data.addProperty("time", log.time);
    data.addProperty("level", log.level);
    data.addProperty("thread", log.thread);
    data.addProperty("logger", log.logger);
    Terminal.event("console-log", data);
  }

  public static void sendOfflineLogs() {
    while (offlineLogs.size() > 0) {
      if (!Terminal.WEBSOCKET.isConnected()) {
        return;
      }
      Log log = offlineLogs.get(0);
      sendLog(log);
      offlineLogs.remove(0);
    }
  }

  public static void onLoad() {
    Logger logger = (Logger) LogManager.getRootLogger();
    logFilter = new TerminalConsoleLogFilter();
    logger.addFilter(logFilter);
  }

  public static void onDisable() {
    logFilter.disable();
  }
}
