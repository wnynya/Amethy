package io.wany.amethy.terminal;

import java.util.ArrayList;
import java.util.List;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jho5245.cucumbery.util.no_groups.AsyncTabCompleter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

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

  public static void command(String client, String input) {
    sendLog(new Log("> " + input, System.currentTimeMillis(), "INFO", "ConsoleCommand", "ConsoleCommand"));
    Bukkit.getScheduler().runTask(Amethy.PLUGIN,
        () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), input));
  }

  public static void tabComplete(String client, String input) {
    JsonObject data = new JsonObject();
    data.addProperty("client", client);

    JsonArray object = new JsonArray();

    String command;
    String[] args;
    try {
      List<String> argsList = new ArrayList<>();
      argsList.addAll(List.of(input.split(" ")));
      command = argsList.get(0);
      argsList.remove(0);
      if (input.endsWith(" ")) {
        argsList.add(" ");
      }
      args = new String[argsList.size()];
      args = argsList.toArray(args);
    } catch (Exception e) {
      return;
    }

    List<String> comp = getCompletes(command, args);
    if (comp != null) {
      for (String s : comp) {
        object.add(s);
      }
    }

    data.add("data", object);
    Terminal.event("console-tabcompleter", data);
  }

  public static List<String> getCompletes(String command, String[] args) {
    List<String> completes = new ArrayList<>();
    PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(command);
    if (pluginCommand == null) {
      return null;
    }
    CommandExecutor commandExecutor = pluginCommand.getExecutor();
    if (commandExecutor instanceof AsyncTabCompleter tabCompleter) {
      List<Completion> completions = tabCompleter.completion(Bukkit.getConsoleSender(), pluginCommand, command, args,
          new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
      for (Completion completion : completions) {
        completes.add(completion.suggestion());
      }
    } else {
      TabCompleter tabCompleter = pluginCommand.getTabCompleter();
      completes = tabCompleter.onTabComplete(Bukkit.getConsoleSender(), pluginCommand, pluginCommand.getLabel(), args);
    }

    return completes;
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
