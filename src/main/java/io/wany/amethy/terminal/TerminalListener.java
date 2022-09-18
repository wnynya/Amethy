package io.wany.amethy.terminal;

import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
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
        Terminal.OPENED = true;
        TerminalDashboard.sendSystemInfo();
        TerminalConsole.sendOfflineLogs();
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

      case "fs-file-download": {
        String path = "";
        try {
          path = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalFilesystem.uploadFile(client, path);
        break;
      }

      case "fs-file-upload": {
        String id = "";
        String path = "";
        try {
          JsonObject obj = data.get("data").getAsJsonObject();
          id = obj.get("id").getAsString();
          path = obj.get("path").getAsString();
        } catch (Exception ignored) {
          ignored.printStackTrace();
        }
        TerminalFilesystem.downloadFile(client, id, path);
        break;
      }

      case "fs-file-delete": {
        String path = "";
        try {
          path = data.get("data").getAsString();
        } catch (Exception ignored) {
        }
        TerminalFilesystem.deleteFile(client, path);
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
