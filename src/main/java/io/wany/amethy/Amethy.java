package io.wany.amethy;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.wany.amethy.commands.*;
import io.wany.amethy.itemonworld.ItemOnWorld;
import io.wany.amethy.listeners.*;
import io.wany.amethy.modules.Config;
import io.wany.amethy.modules.ServerPropertiesSorter;
import io.wany.amethy.modules.StopServer;
import io.wany.amethy.modules.Updater;
import io.wany.amethy.modules.amethy.Database;
import io.wany.amethy.supports.coreprotect.CoreProtectSupport;
import io.wany.amethy.supports.cucumbery.CucumberySupport;
import io.wany.amethy.supports.vault.VaultSupport;
import io.wany.amethy.sync.Sync;
import io.wany.amethy.terminal.Terminal;
import io.wany.amethy.wand.Wand;
import io.wany.amethy.wand.command.WandEditCommand;
import io.wany.amethy.wand.command.WandEditTabCompleter;

/**
 *
 * Amethy
 * https://amethy.wany.io
 * 
 * ©2021 - 2023 Wany <sung@wany.io> (https://wany.io)
 *
 */
public class Amethy extends JavaPlugin {

  public static Amethy PLUGIN;

  public static final String COLOR = "#D2B0DD;";
  public static final String PREFIX = COLOR + "&l[Amethy]:&r ";
  public static final String PREFIX_CONSOLE = "[Amethy] ";
  public static final UUID UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");
  public static final String API = "https://api.wany.io/amethy";
  public static final int CONFIG_VERSION = 100;

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
    Database.onLoad();

  }

  @Override
  public void onEnable() {

    Terminal.onEnable();

    Sync.onEnable();

    Wand.onEnable();
    ItemOnWorld.onEnable();

    registerCommand("amethy", new AmethyCommand(), new AmethyTabCompleter());

    registerCommand("wandedit", new WandEditCommand(), new WandEditTabCompleter());

    registerCommand("bungeeteleport", new BungeeTeleportCommand(), new AmethyTabCompleter());
    registerCommand("closeinvenrory", new CloseinventoryCommand(), new AmethyTabCompleter());
    registerCommand("drop", new DropCommand(), new AmethyTabCompleter());
    registerCommand("exit", new ExitCommand(), new AmethyTabCompleter());
    registerCommand("drop", new DropCommand(), new AmethyTabCompleter());
    registerCommand("lid", new LidCommand(), new AmethyTabCompleter());
    registerCommand("list", new ListCommand(), new AmethyTabCompleter());
    registerCommand("toggledownfall", new ToggledownfallCommand(), new AmethyTabCompleter());

    registerEvent(new PlayerJoin());
    registerEvent(new PlayerQuit());
    registerEvent(new PlayerChat());
    registerEvent(new PlayerDeath());
    registerEvent(new PlayerInteract());
    registerEvent(new PlayerMove());
    registerEvent(new BlockBreak());
    registerEvent(new BlockPhysics());
    registerEvent(new BlockPistonExtend());
    registerEvent(new BlockDestroy());
    registerEvent(new BlockDropItem());
    registerEvent(new BlockExplode());
    registerEvent(new EntityAddToWorld());
    registerEvent(new ItemSpawn());
    registerEvent(new InventoryClick());
    registerEvent(new EntityDeath());
    registerEvent(new PluginEnable());
    registerEvent(new PluginDisable());

    VaultSupport.onEnable();
    CucumberySupport.onEnable();
    CoreProtectSupport.onEnable();

    FILE = this.getFile();
    DIR = this.getDataFolder().getAbsoluteFile();
    PLUGINS_DIR = new File(this.getDataFolder().getAbsoluteFile().getParent());
    SERVER_DIR = new File(new File(this.getDataFolder().getAbsoluteFile().getParent()).getParent());

    Updater.onEnable();
    ServerPropertiesSorter.onEnable();

    StopServer.onEnable();

    try {
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onDisable() {

    Wand.onDisable();

    VaultSupport.onDisable();
    CucumberySupport.onDisable();
    CoreProtectSupport.onDisable();

    Updater.onDisable();

    Sync.onDisable();

    Database.onDisable();

    Terminal.onDisable();

    StopServer.onDisable();

  }

  public void registerCommand(String cmd, CommandExecutor exc, TabCompleter tab) {
    Map<String, Map<String, Object>> map = PLUGIN.getDescription().getCommands();
    if (map.containsKey(cmd)) {
      PluginCommand pc = this.getCommand(cmd);
      if (pc == null) {
        return;
      }
      pc.setExecutor(exc);
      pc.setTabCompleter(tab);
    }
  }

  public void registerEvent(Listener l) {
    PluginManager pm = Bukkit.getServer().getPluginManager();
    pm.registerEvents(l, this);
  }

}
