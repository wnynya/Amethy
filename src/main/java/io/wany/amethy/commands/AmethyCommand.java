package io.wany.amethy.commands;

import io.wany.amethy.Amethy;
import io.wany.amethy.gui.Menu;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.Message;
import io.wany.amethy.modules.PluginLoader;
import io.wany.amethy.modules.Updater;
import io.wany.amethy.modules.Updater.Version;
import io.wany.amethy.terminal.Terminal;

import java.io.FileNotFoundException;

import com.google.gson.JsonObject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AmethyCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (args.length == 0) {
      sender.sendMessage(Message.commandErrorTranslatable("command.unknown.command"));
      sender.sendMessage(Message.commandErrorArgsComponent(label, args, -1));
      return true;
    }

    switch (args[0].toLowerCase()) {

      case "version", "v" -> {
        if (!sender.hasPermission("amethy.version")) {
          return true;
        }
        String tail = "";
        if (Updater.isLatest() != null && Updater.isLatest() == true) {
          tail = "[Latest]";
        } else if (Updater.isLatest() != null && Updater.isLatest() == false) {
          tail = "[Outdated]";
        }
        sender.sendMessage(Message.of("Amethy v" + Amethy.PLUGIN.getDescription().getVersion() + " " + tail));
        return true;
      }

      case "reload", "r" -> {
        if (!sender.hasPermission("amethy.reload")) {
          return true;
        }
        sender.sendMessage(Message.of("Reloading Amethy v" + Amethy.PLUGIN.getDescription().getVersion()));
        long s = System.currentTimeMillis();
        PluginLoader.unload();
        PluginLoader.load(Amethy.FILE);
        long e = System.currentTimeMillis();
        sender.sendMessage(Message.of("Reload complete &7(" + (e - s) + "ms)"));
        return true;
      }

      case "terminal" -> {
        if (!sender.hasPermission("amethy.terminal")) {
          return true;
        }

        if (args.length > 1) {
          switch (args[1].toLowerCase()) {
            case "grant" -> {
              if (!sender.hasPermission("amethy.terminal.grant")) {
                return true;
              }

              if (args.length <= 2) {
                return true;
              }

              String id = args[2];

              try {
                JsonObject aci = Terminal.grant(id);
                Console.log(aci.toString());
                JsonObject data = aci.get("data").getAsJsonObject();
                String gmsg = "";
                JsonObject from = null;
                if (data.has("from")) {
                  from = data.get("from").getAsJsonObject();
                  gmsg += from.get("eid").getAsString() + " -> ";
                }
                JsonObject to = data.get("to").getAsJsonObject();
                gmsg += to.get("eid").getAsString();
                Message.send(sender, Message.parse("Grant success: " + gmsg));
              } catch (FileNotFoundException e) {
                Message.send(sender, Message.parse("Fail to grant: Account not found"));
              } catch (Exception e) {
                Message.send(sender, Message.parse("Fail to grant: Unknown"));
              }
            }

            default -> {
              sender.sendMessage(Message.commandErrorTranslatable("command.unknown.argument"));
              sender.sendMessage(Message.commandErrorArgsComponent(label, args, 1));
              return true;
            }
          }

          return true;
        } else {
          sender.sendMessage(Message.commandErrorTranslatable("command.unknown.command"));
          sender.sendMessage(Message.commandErrorArgsComponent(label, args, 1));
          return true;
        }
      }

      case "menu", "m" -> {
        if (!sender.hasPermission("amethy.menu")) {
          return true;
        }
        if (!(sender instanceof Player player)) {
          return true;
        }

        Menu.show(player, Menu.Main.inventory(player));
        return true;
      }

      case "update" -> {
        try {
          Version version = Updater.defaultUpdater.getLatestVersion();
          Updater.defaultUpdater.updateVersion(version);
          sender.sendMessage(Message.of("업데이트 완료"));
        } catch (Exception e) {
          sender.sendMessage(Message.of("업데이트 중 오류 발생 망함 펑 (콘솔에 프린트스택트레이스 함)"));
          e.printStackTrace();
        }
        return true;
      }

      default -> {
        sender.sendMessage(Message.commandErrorTranslatable("command.unknown.argument"));
        sender.sendMessage(Message.commandErrorArgsComponent(label, args, 0));
        return true;
      }

    }

  }

}