package io.wany.amethy.supports;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;

import io.wany.amethyst.EventEmitter;

public class PluginSupport extends EventEmitter {

  private static HashMap<String, PluginSupport> supports = new HashMap<>();

  private String name;
  private Plugin plugin;

  private boolean enabled = false;
  private boolean loaded = false;

  public PluginSupport(String name) {
    super();

    this.name = name;
    supports.put(name, this);
  }

  private void check() {
    this.plugin = Bukkit.getPluginManager().getPlugin(this.name);
    if (this.plugin != null && this.plugin.isEnabled()) {
      if (!this.enabled) {
        this.enabled = true;
        if (!this.loaded) {
          this.loaded = true;
          this.emit("enable", this);
        } else {
          this.emit("reload", this);
        }
      }
    } else {
      if (this.enabled) {
        this.enabled = false;
        this.emit("disable", this);
      }
    }
  }

  public void ready() {
    this.check();
  }

  private void onPlugin(PluginEvent event) {
    Plugin plugin = event.getPlugin();
    if (!plugin.getName().equals(this.name)) {
      return;
    }
    this.check();
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public Plugin getPlugin() {
    return this.plugin;
  }

  public static void onPluginEvent(PluginEvent event) {
    supports.values().forEach((support) -> {
      support.onPlugin(event);
    });
  }

  public static PluginSupport of(String name) {
    return supports.get(name);
  }

}
