package io.wany.amethy;

import io.wany.amethy.commands.*;
import io.wany.amethy.listeners.*;
import io.wany.amethy.modules.*;
import io.wany.amethy.modules.database.Database;
import io.wany.amethy.modules.itemonworld.ItemOnWorld;
import io.wany.amethy.modules.sync.Sync;
import io.wany.amethy.modules.wand.Wand;
import io.wany.amethy.modules.wand.command.WandEditCommand;
import io.wany.amethy.modules.wand.command.WandEditTabCompleter;
import io.wany.amethy.supports.coreprotect.CoreProtectSupport;
import io.wany.amethy.supports.cucumbery.CucumberySupport;
import io.wany.amethy.supports.essentials.EssentialsSupport;
import io.wany.amethy.supports.vault.VaultSupport;
import io.wany.amethyst.Json;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

/**
 * Amethy
 * https://amethy.wany.io
 * <p></p>
 * ©2021 - 2023 Wany <sung@wany.io> (https://wany.io)
 */
public class Amethy extends JavaPlugin {

  public static Amethy PLUGIN;

  public static final String NAME = "아메시";
  public static final String PREFIX = "§x§d§2§b§0§d§d§l[" + NAME + "]:§r ";
  public static final String PREFIX_CONSOLE = "[" + NAME + "] ";
  public static final UUID UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");
  protected static final boolean ISRELOAD = Bukkit.getWorlds().size() != 0;

  public static String VERSION;
  public static File FILE;
  public static File PLUGINS_DIR;
  public static File SERVER_DIR;
  public static Json CONFIG;
  public static boolean DEBUG = false;
  private static int JAVA_VERSION;
  private static boolean DISABLED = false;
  public static boolean PAPERAPI;
  public static ServerMessage MESSAGE;

  public static final int YAMLCONFIG_VERSION = 100;
  public static FileConfiguration YAMLCONFIG;

  @SuppressWarnings("deprecation")
  @Override
  public void onLoad() {

    if (PLUGIN != null) {
      return;
    }

    PLUGIN = this;

    VERSION = PLUGIN.getDescription().getVersion();
    FILE = PLUGIN.getFile().getAbsoluteFile();
    PLUGINS_DIR = FILE.getParentFile();
    SERVER_DIR = PLUGINS_DIR.getParentFile();

    CONFIG = new Json(PLUGINS_DIR.toPath().resolve("Amethy/amethy.json").toFile());

    if (CONFIG.has("debug")) {
      DEBUG = CONFIG.getBoolean("debug");
    }
    else {
      CONFIG.set("debug", false);
    }

    YAMLCONFIG = YamlConfig.onLoad();

    String javaVersion = System.getProperty("java.version");
    if (javaVersion.startsWith("1.")) {
      javaVersion = javaVersion.substring(2, 3);
    }
    else {
      int dot = javaVersion.indexOf(".");
      if (dot != -1) {
        javaVersion = javaVersion.substring(0, dot);
      }
    }
    JAVA_VERSION = Integer.parseInt(javaVersion);

    if (JAVA_VERSION < 17) {
      DISABLED = true;
      return;
    }

    String version = Bukkit.getServer().getVersion().toLowerCase();
    PAPERAPI = version.contains("paper") || version.contains("purpur") || version.contains("pufferfish");

    MESSAGE = PAPERAPI ? new PaperMessage() : new SpigotMessage();

    Database.onLoad();

  }

  @Override
  public void onEnable() {

    if (DISABLED) {
      console.error("Plugin requires Java version >= 17 to run. Disable plugin.");
      PluginLoader.unload(PLUGIN);
      return;
    }

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
    registerEvent(new EntityDeath());
    registerEvent(new PluginEnable());
    registerEvent(new PluginDisable());

    if (PAPERAPI) {
      registerEvent(new PaperPlayerChat());
    }
    else {
      console.warn("서버에서 Paper API를 확인할 수 없습니다. 일부 기능이 비활성화됩니다.");
      registerEvent(new SpigotPlayerChat());
    }

    CucumberySupport.onEnable();
    VaultSupport.onEnable();
    EssentialsSupport.onEnable();

    Updater.onEnable();
    Sync.onEnable();
    Wand.onEnable();
    ItemOnWorld.onEnable();

    ServerPropertiesSorter.onEnable();

    try {
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onDisable() {

    Updater.onDisable();
    Sync.onDisable();
    Wand.onDisable();

    CoreProtectSupport.onDisable();

    Database.onDisable();
  }

  public void registerCommand(String cmd, CommandExecutor exc, TabCompleter tab) {
    PluginCommand pc = null;

    if (true) {
      pc = this.getCommand(cmd);
    }
    else {
      // This code is preserved to prepare future where paper-plugin.yml became mandatory
      CommandMap cmdMap = Bukkit.getCommandMap();
      String prefix = this.getName().toLowerCase();

      try {
        var clazz = Class.forName("org.bukkit.command.PluginCommand");
        var constr = clazz.getDeclaredConstructor(String.class, Plugin.class);
        constr.setAccessible(true);
        pc = (PluginCommand) constr.newInstance(cmd, this);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      cmdMap.register(pc.getName(), prefix, pc);

      // this fails to register alias! Needs fix
      for (String alias : pc.getAliases()) {
        cmdMap.register(alias, prefix, pc);
      }
      // ----- END OF PRESERVED CODE ----- //
    }

    if (pc != null) {
      pc.setExecutor(exc);
      pc.setTabCompleter(tab);
    }
    else {
      System.out.printf("Failed to load command: %s", cmd);
    }
  }

  public void registerEvent(Listener l) {
    PluginManager pm = Bukkit.getServer().getPluginManager();
    pm.registerEvents(l, this);
  }

  private void assignServerType() {
    String version = Bukkit.getServer().getVersion().toLowerCase();
    PAPERAPI = version.contains("paper") || version.contains("pufferfish");
  }

}
