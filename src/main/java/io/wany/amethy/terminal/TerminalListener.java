package io.wany.amethy.terminal;

import com.google.gson.JsonObject;

import io.wany.amethy.Console;

public class TerminalListener {

  public static void on(String event, String message, JsonObject data) {

    Console.log("ws: " + event);
    String client = "";
    try {
      data.get("client").getAsString();
    } catch (Exception ignored) {
    }

    switch (event) {

      case "init": {
        TerminalDashboard.sendSystemInfo();
        TerminalConsole.sendOfflineLogs();
        break;
      }

      case "fs-dir-info": {
        String path = "";
        try {
          data.get("path").getAsString();
        } catch (Exception ignored) {
        }
        TerminalFilesystem.sendDirectoryInfo(client, path);
        break;
      }

    }
  }
}
