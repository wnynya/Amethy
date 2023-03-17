package io.wany.amethy;

import org.apache.logging.log4j.core.config.plugins.util.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class BukkitPluginLoader {

  public static void unload() {
    Plugin plugin = Amethy.PLUGIN;
    String name = plugin.getName();
    // commandwrap
    PluginManager pluginManager = Bukkit.getPluginManager();
    SimpleCommandMap commandMap = null;
    List<Plugin> plugins = null;
    Map<String, Plugin> names = null;
    Map<String, Command> commands = null;
    Map<Event, SortedSet<RegisteredListener>> listeners = null;
    boolean reloadlisteners = true;
    if (pluginManager != null) {
      pluginManager.disablePlugin(plugin);
      try {
        Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        plugins = (List<Plugin>) pluginsField.get(pluginManager);
        Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
        lookupNamesField.setAccessible(true);
        names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
        try {
          Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
          listenersField.setAccessible(true);
          listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
        } catch (Exception e) {
          reloadlisteners = false;
        }
        Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        commands = (Map<String, Command>) knownCommandsField.get(commandMap);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    pluginManager.disablePlugin(plugin);
    if (listeners != null && reloadlisteners) {
      for (SortedSet<RegisteredListener> set : listeners.values()) {
        set.removeIf(value -> value.getPlugin() == plugin);
      }
    }
    if (commandMap != null)
      for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
        Map.Entry<String, Command> entry = it.next();
        if (entry.getValue() instanceof PluginCommand) {
          PluginCommand c = (PluginCommand) entry.getValue();
          if (c.getPlugin() == plugin) {
            c.unregister(commandMap);
            it.remove();
          }
        } else {
          try {
            Field pluginField = Arrays.stream(entry.getValue().getClass().getDeclaredFields())
                .filter(field -> Plugin.class.isAssignableFrom(field.getType())).findFirst().orElse(null);
            if (pluginField != null) {
              Plugin owningPlugin;
              try {
                pluginField.setAccessible(true);
                owningPlugin = (Plugin) pluginField.get(entry.getValue());
                if (owningPlugin.getName().equalsIgnoreCase(plugin.getName())) {
                  entry.getValue().unregister(commandMap);
                  it.remove();
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equalsIgnoreCase("zip file closed")) {
              entry.getValue().unregister(commandMap);
              it.remove();
            }
          }
        }
      }
    if (plugins != null && plugins.contains(plugin)) {
      plugins.remove(plugin);
    }
    if (names != null && names.containsKey(name)) {
      names.remove(name);
    }
    ClassLoader cl = plugin.getClass().getClassLoader();
    if (cl instanceof URLClassLoader) {
      try {
        Field pluginField = cl.getClass().getDeclaredField("plugin");
        pluginField.setAccessible(true);
        pluginField.set(cl, null);
        Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
        pluginInitField.setAccessible(true);
        pluginInitField.set(cl, null);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        ((URLClassLoader) cl).close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    System.gc();
  }

  public static void load(File file) {
    Plugin plugin = null;
    if (!file.isFile()) {
      return;
    }
    try {
      plugin = Bukkit.getPluginManager().loadPlugin(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (plugin == null) {
      return;
    }
    plugin.onLoad();
    Bukkit.getPluginManager().enablePlugin(plugin);
  }

}
