package io.wany.amethy.commands;

import io.wany.amethy.Amethy;
import io.wany.amethy.Console;
import io.wany.amethy.Message;
import io.wany.amethy.modules.Promise;
import io.wany.amethy.modules.Request;
import io.wany.amethy.modules.Request.Options;
import io.wany.amethy.st.PluginLoader;
import io.wany.amethy.terminal.Terminal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AmethyCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (args.length == 0) {
      return true;
    }

    switch (args[0].toLowerCase()) {

      case "version", "v" -> {
        if (!sender.hasPermission("amethy.version")) {
          return true;
        }
        Message.info(sender, Amethy.PREFIX + Amethy.PLUGIN.getDescription().getVersion());
        return true;
      }

      case "reload", "r" -> {
        if (!sender.hasPermission("amethy.reload")) {
          return true;
        }
        Message.info(sender, Amethy.PREFIX + "Reloading Amethy v." + Amethy.PLUGIN.getDescription().getVersion());
        long s = System.currentTimeMillis();
        // Terminal.STATUS = Terminal.Status.RELOAD;
        PluginLoader.unload();
        PluginLoader.load(Amethy.FILE);
        long e = System.currentTimeMillis();
        Message.info(sender, Amethy.PREFIX + "Reload complete &7(" + (e - s) + "ms)");
        return true;
      }

      case "terminal" -> {
        if (!sender.hasPermission("amethy.terminal")) {
          return true;
        }

        if (args.length > 1) {
          switch (args[1].toLowerCase()) {
            case "grant": {
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
          }
        }

        return true;
      }

      /*
       * case "update", "u" -> {
       * if (!sender.hasPermission("amethy.update")) {
       * return true;
       * }
       * 
       * ExecutorService e = Executors.newFixedThreadPool(1);
       * e.submit(() -> {
       * 
       * boolean silent = false;
       * boolean force = false;
       * if (args.length > 1) {
       * for (String str : args) {
       * if (str.equalsIgnoreCase("-silent") || str.equalsIgnoreCase("-s")) {
       * silent = true;
       * }
       * if (str.equalsIgnoreCase("-force") || str.equalsIgnoreCase("-f")) {
       * force = true;
       * }
       * }
       * }
       * 
       * long s = System.currentTimeMillis();
       * Updater.Version version = null;
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Check latest " + Amethy.COLOR +
       * Updater.defaultUpdater.getChannelName() + "&r version . . . ");
       * }
       * try {
       * version = Updater.defaultUpdater.getLatestVersion();
       * }
       * catch (Updater.NotFoundException exception) {
       * Message.warn(sender, Amethy.PREFIX +
       * "&eError on updater/check: Build Not Found");
       * }
       * catch (Updater.InternalServerErrorException exception) {
       * Message.warn(sender, Amethy.PREFIX +
       * "&eError on updater/check: Internal Server Error");
       * }
       * catch (SocketTimeoutException exception) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/check: Timed Out");
       * }
       * catch (IOException exception) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/check: IO");
       * }
       * catch (ParseException exception) {
       * Message.warn(sender, Amethy.PREFIX +
       * "&eError on updater/check: Data Parse Failed");
       * }
       * catch (Exception e1) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/check: Unknown");
       * }
       * 
       * if (version != null) {
       * 
       * if (Amethy.PLUGIN.getDescription().getVersion().equals(version.name) &&
       * !force) {
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Already latest version " + Amethy.COLOR
       * + Amethy.PLUGIN.getDescription().getVersion() + "");
       * }
       * }
       * else {
       * 
       * if (Amethy.PLUGIN.getDescription().getVersion().equals(version.name)) {
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Already latest version " + Amethy.COLOR
       * + Amethy.PLUGIN.getDescription().getVersion() +
       * "&r, but it forces to update");
       * }
       * }
       * else {
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Found new latest version " +
       * Amethy.COLOR + version.name);
       * }
       * }
       * 
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Downloading file . . . ");
       * }
       * try {
       * version.download();
       * }
       * catch (SecurityException exception) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/download: Denied");
       * version = null;
       * }
       * catch (FileNotFoundException exception) {
       * Message.warn(sender, Amethy.PREFIX +
       * "&eError on updater/download: File Not Found");
       * version = null;
       * }
       * catch (IOException exception) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/download: IO");
       * version = null;
       * }
       * catch (Exception e1) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/download: Unknown");
       * version = null;
       * }
       * 
       * if (version != null) {
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Update plugin . . . ");
       * }
       * try {
       * version.update();
       * }
       * catch (IOException exception) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/update: IO");
       * version = null;
       * }
       * catch (Exception e1) {
       * Message.warn(sender, Amethy.PREFIX + "&eError on updater/update: Unknown");
       * version = null;
       * }
       * 
       * if (version != null) {
       * long e1 = System.currentTimeMillis();
       * 
       * if (!silent) {
       * Message.info(sender, Amethy.PREFIX + "Update Success " + Amethy.COLOR +
       * Amethy.PLUGIN.getDescription().getVersion() + "&r => " + Amethy.COLOR +
       * version.name + "&r &7(" + (e1 - s) + "ms)");
       * }
       * }
       * 
       * }
       * 
       * }
       * 
       * }
       * 
       * });
       * 
       * return true;
       * }
       * 
       * case "menu", "m" -> {
       * if (!sender.hasPermission("amethy.menu")) {
       * return true;
       * }
       * if (!(sender instanceof Player player)) {
       * return true;
       * }
       * 
       * Menu.show(player, Menu.Main.inventory(player));
       * return true;
       * }
       */
      case "test" -> {
        if (!sender.hasPermission("amethy.test")) {
          return true;
        }

        return true;
      }

      default -> {
        return true;
      }

    }

  }

  public static Promise test() {
    return new Promise((resolve, reject) -> {
      resolve.accept(200);
    });
  }

}