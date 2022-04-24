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

/**
 * Hello world!
 *
 */
public class Amethy extends JavaPlugin {

  public static Amethy PLUGIN;

  public static final String COLOR = "#D2B0DD;";
  public static final String PREFIX = COLOR + "&l[Amethy]:&r ";
  public static final UUID UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");

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

  }

  @Override
  public void onEnable() {


  }

  @Override
  public void onDisable() {


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
