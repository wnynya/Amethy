package io.wany.amethy;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.wany.amethy.commands.AmethyCommand;
import io.wany.amethy.commands.AmethyTabCompleter;
import io.wany.amethy.st.ServerPropertiesSorter;
import io.wany.amethy.terminal.Terminal;

/**
 *
 * Amethy ©2022 Wany (sung@wany.io)
 * https://amethy.wany.io
 *
 */
public class Amethy extends JavaPlugin {

  public static Amethy PLUGIN;

  public static final String COLOR = "#D2B0DD;";
  public static final String PREFIX = COLOR + "&l[Amethy]:&r ";
  public static final UUID UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");
  public static final String API = "https://api.wany.io/amethy";

  public static boolean DEBUG = false;
  public static boolean NIGHT = false;

  public static FileConfiguration CONFIG;
  public static File FILE;
  public static File DIR;
  public static File PLUGINS_DIR;
  public static File SERVER_DIR;

  @Override
  public void onLoad() {

    PLUGIN = this;
    CONFIG = Config.onLoad();
    Terminal.onLoad();

  }

  @Override
  public void onEnable() {

    registerCommand("amethy", new AmethyCommand(), new AmethyTabCompleter());

    FILE = this.getFile();
    DIR = this.getDataFolder().getAbsoluteFile();
    PLUGINS_DIR = new File(this.getDataFolder().getAbsoluteFile().getParent());
    SERVER_DIR = new File(new File(this.getDataFolder().getAbsoluteFile().getParent()).getParent());

    Updater.onEnable();
    ServerPropertiesSorter.onEnable();

    Terminal.onEnable();

  }

  @Override
  public void onDisable() {

    Updater.onDisable();

    Terminal.onDisable();

  }

  public void registerCommand(String command, CommandExecutor cmdExc, org.bukkit.command.TabCompleter cmdTab) {
    Map<String, Map<String, Object>> commandMap = PLUGIN.getDescription().getCommands();
    if (commandMap.containsKey(command)) {
      PluginCommand pluginCommand = this.getCommand(command);
      if (pluginCommand == null) {
        return;
      }
      pluginCommand.setExecutor(cmdExc);
      pluginCommand.setTabCompleter(cmdTab);
    }
  }

  public void registerEvent(Listener eventListener) {
    PluginManager pm = Bukkit.getServer().getPluginManager();
    pm.registerEvents(eventListener, this);
  }

}
