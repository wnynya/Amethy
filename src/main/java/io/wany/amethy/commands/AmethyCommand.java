package io.wany.amethy.commands;

import io.wany.amethy.Amethy;
import io.wany.amethy.BukkitPluginLoader;
import io.wany.amethy.Console;
import io.wany.amethy.Updater;
import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AmethyCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (args.length == 0) {
      // 오류: args[0] 필요
      error(sender, "Insufficient arguments");
      info(sender, "Usage: /" + label + " (version|reload|debug|update|updater)");
      return true;
    }

    switch (args[0].toLowerCase()) {

      case "version" -> {
        if (!sender.hasPermission("amethy.version")) {
          // 오류: 권한 없음
          error(sender, "You don't have permission");
          return true;
        }
        String tail = "";
        try {
          if (Updater.isLatest()) {
            tail = "[LATEST]";
          } else {
            tail = "[OUTDATED]";
          }
        } catch (Exception e) {
          tail = "[VERSION CHECK FAILED]";
        }
        // 정보: 플러그인 버전
        info(sender, Amethy.NAME + " v" + Amethy.VERSION + " " + tail);
        if (tail.equals("[OUTDATED]") && sender instanceof Player player) {
        }
        return true;
      }

      case "reload" -> {
        if (!sender.hasPermission("amethy.reload")) {
          // 오류: 권한 없음
          error(sender, "You don't have permission");
          return true;
        }
        // 정보: 플러그인 리로드 시작
        info(sender, "Reloading " + Amethy.NAME + " v" + Amethy.VERSION);
        long s = System.currentTimeMillis();
        BukkitPluginLoader.unload();
        BukkitPluginLoader.load(Amethy.FILE);
        long e = System.currentTimeMillis();
        // 정보: 플러그인 리로드 완료
        info(sender, "Reload complete (" + (e - s) + "ms)");
        return true;
      }

      case "debug" -> {
        if (!sender.hasPermission("amethy.debug")) {
          // 오류: 권한 없음
          error(sender, "You don't have permission");
          return true;
        }
        boolean next = Amethy.DEBUG;
        if (args.length >= 2) {
          if (args[1].toLowerCase().equals("enable")) {
            next = true;
          } else if (args[1].toLowerCase().equals("disable")) {
            next = false;
          } else {
            // 오류: 알 수 없는 args[1]
            error(sender, "Unknown argument");
            info(sender, "Usage: /" + label + " " + args[0] + " (enable|disable)");
            return true;
          }
          Amethy.DEBUG = next;
          Amethy.CONFIG.set("debug", Amethy.DEBUG);
          // 정보: 변경된 디버그 메시지 표시 여부
          info(sender, "Debug " + (next ? "en" : "dis") + "abled");
          return true;
        } else {
          // 정보: 현재 디버그 메시지 표시 여부
          info(sender, "Debug is currently " + (next ? "en" : "dis") + "abled");
          return true;
        }
      }

      case "update" -> {
        if (!sender.hasPermission("amethy.updater.update")) {
          // 오류: 권한 없음
          error(sender, "You don't have permission");
          return true;
        }
        String version;
        try {
          version = Updater.getLatest();
        } catch (Exception e) {
          // 오류: 버전 확인 실패
          error(sender, "Version check failed " + e.getMessage());
          return true;
        }
        if (Amethy.VERSION.equals(version)) {
          if (args.length >= 2 && args[1].toLowerCase().equals("-force")) {
          } else {
            // 경고: 이미 최신 버전임
            warn(sender, "It's already the latest version");
            warn(sender, "Use -force flag to update force");
            return true;
          }
        }
        // 정보: 플러그인 버전
        info(sender, "Found newer version of plugin");
        info(sender, "  Current: " + Amethy.NAME + " v" + Amethy.VERSION);
        info(sender, "  Latest: " + Amethy.NAME + " v" + version);
        // 정보: 파일 다운로드 시작
        info(sender, "Downloading file...");
        File file;
        try {
          file = Updater.download(version);
        } catch (Exception e) {
          // 오류: 파일 다운로드 실패
          error(sender, "File download failed " + e.getMessage());
          return true;
        }
        // 정보: 파일 다운로드 완료
        info(sender, "Download complete");
        // 정보: 플러그인 업데이트 시작
        info(sender, "Updating plugin...");
        try {
          Updater.update(file, version);
        } catch (Exception e) {
          // 오류: 업데이트 실패
          error(sender, "Plugin update failed " + e.getMessage());
          return true;
        }
        // 정보: 업데이트 완료
        info(sender, "Update complete");
        return true;
      }

      case "updater" -> {
        if (!sender.hasPermission("amethy.updater")) {
          // 오류: 권한 없음
          error(sender, "You don't have permission");
          return true;
        }

        if (args.length >= 2) {
          if (args[1].toLowerCase().equals("automation")) {
            boolean next = Updater.AUTOMATION;
            if (args.length >= 3) {
              if (args[2].toLowerCase().equals("enable")) {
                next = true;
              } else if (args[2].toLowerCase().equals("disable")) {
                next = false;
              } else {
                // 오류: 알 수 없는 args[2]
                error(sender, "Unknown argument");
                info(sender, "Usage: /" + label + " " + args[0] + " " + args[1] + " [enable|disable]");
                return true;
              }
              Updater.AUTOMATION = next;
              Amethy.CONFIG.set("updater.automation", Updater.AUTOMATION);
              // 정보: 변경된 업데이터 자동화 여부
              info(sender, "Updater automation " + (next ? "en" : "dis") + "abled");
              return true;
            } else {
              // 정보: 현재 업데이터 자동화 여부
              info(sender, "Updater automation is currently " + (next ? "en" : "dis") + "abled");
              return true;
            }
          } else if (args[1].toLowerCase().equals("channel")) {
            String next = Updater.CHANNEL;
            if (args.length >= 3) {
              if (args[2].toLowerCase().equals("release")) {
                next = "release";
              } else if (args[2].toLowerCase().equals("dev")) {
                next = "dev";
              } else {
                // 오류: 알 수 없는 args[2]
                error(sender, "Unknown argument");
                info(sender, "Usage: /" + label + " " + args[0] + " " + args[1] + " [release|dev]");
                return true;
              }
              Updater.CHANNEL = next;
              Amethy.CONFIG.set("updater.channel", Updater.CHANNEL);
              // 정보: 변경된 업데이터 채널
              info(sender, "Updater channel changed to " + next);
              return true;
            } else {
              // 정보: 현재 업데이터 채널
              info(sender, "Current updater channel is " + Updater.CHANNEL);
              return true;
            }
          } else {
            // 오류: 알 수 없는 args[1]
            error(sender, "Unknown argument");
            info(sender, "Usage: /" + label + " " + args[0] + " (channel|automation)");
            return true;
          }
        } else {
          // 오류: args[1] 필요
          error(sender, "Insufficient arguments");
          info(sender, "Usage: /" + label + " " + args[0] + " (channel|automation)");
          return true;
        }
      }

      default -> {
        // 오류 알 수 없는 args[0]
        error(sender, "Unknown argument");
        info(sender, "Usage: /" + label + " (version|reload|debug|update|updater)");
        return true;
      }

    }

  }

  public void info(CommandSender sender, String message) {
    if (sender instanceof Player player) {
      player.sendMessage(Amethy.PREFIX + message);
    } else {
      Console.info(message);
    }
  }

  public void warn(CommandSender sender, String message) {
    if (sender instanceof Player player) {
      player.sendMessage(Amethy.PREFIX + "§e" + message);
    } else {
      Console.warn(message);
    }
  }

  public void error(CommandSender sender, String message) {
    if (sender instanceof Player player) {
      player.sendMessage(Amethy.PREFIX + "§c" + message);
    } else {
      Console.error(message);
    }
  }

}