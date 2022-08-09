package io.wany.amethy.terminal;

import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;
import io.wany.amethy.terminal.TerminalConsole.Log;

public class TerminalListener {

  public static void on(String event, String message, JsonObject data) {

    String client = "";
    try {
      client = data.get("client").getAsString();
    } catch (Exception ignored) {
    }

    switch (event) {

      case "init": {
        TerminalDashboard.sendSystemInfo();
        TerminalConsole.sendOfflineLogs();
        if (Terminal.ISRELOAD) {
          Terminal.ISRELOAD = false;
          TerminalConsole.sendLog(new Log(
              "Enabling Amethy v" + Amethy.PLUGIN.getDescription().getVersion(), System.currentTimeMillis(), "INFO",
              "Server thread", "Amethy"));
        }
        break;
      }

      case "console-command": {
        String command = "";
        try {
          command = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalConsole.command(client, command);
        break;
      }

      case "console-tabcompleter": {
        String command = "";
        try {
          command = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalConsole.tabComplete(client, command);
        break;
      }

      case "fs-dir-info": {
        String path = "";
        try {
          path = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalFilesystem.sendDirectoryInfo(client, path);
        break;
      }

      case "players-target": {
        String uuid = "";
        try {
          uuid = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalPlayers.sendPlayer(client, uuid);
        break;
      }

    }
  }
}
