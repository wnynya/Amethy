package io.wany.amethy.modules.portal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modulesmc.Console;
import io.wany.amethyst.Json;

public class Portal {

  private static boolean ENABLED = false;

  private String id;
  private List<Location> area;
  private List<String> commands;

  protected Portal(String id) {
    this.id = id;
  }

  protected void save() {
    Json data = new Json();
    List<Json> jsonArea = new ArrayList<>();
    for (Location loc : this.area) {
      Json jsonLoc = new Json();
      jsonLoc.set("world", loc.getWorld().getName());
      jsonLoc.set("x", loc.getX());
      jsonLoc.set("y", loc.getY());
      jsonLoc.set("z", loc.getZ());
      jsonArea.add(jsonLoc);
    }
    data.set("area", jsonArea);
    data.set("commands", this.commands);
  }

  protected String getID() {
    return this.id;
  }

  protected void setArea(List<Location> area) {
    this.area = area;
  }

  protected boolean trigger(Location loc) {
    return area.contains(loc);
  }

  protected List<String> getCommands() {
    return this.commands;
  }

  protected void setCommands(List<String> commands) {
    this.commands = commands;
  }

  protected void execute(Player player) {
    for (String command : this.commands) {
      Bukkit.dispatchCommand(player, command);
    }
  }

  private static File storage;
  private static HashMap<String, Portal> portals = new HashMap<>();

  public static void onEnable() {
    ENABLED = true;

    try {
      storage = Amethy.PLUGIN_DIR.toPath().resolve("portal").toFile();
      storage.mkdirs();
    } catch (Throwable t) {
      t.printStackTrace();
    }

    File[] files = storage.listFiles();
    for (File file : files) {
      String id = file.getName();
      if (id.startsWith("-")) {
        continue;
      }
      try {
        Json data = new Json(file);
        List<Json> jsonArea = data.getJsonList("area");
        List<Location> area = new ArrayList<>();
        for (Json jsonLoc : jsonArea) {
          area.add(new Location(
              Bukkit.getWorld(jsonLoc.getString("world")),
              jsonLoc.getDouble("x"),
              jsonLoc.getDouble("y"),
              jsonLoc.getDouble("z")));
        }
        List<String> commands = data.getStringList("commands");
        Portal portal = new Portal(id);
        portal.setArea(area);
        portal.setCommands(commands);
        portals.put(id, portal);
        Console.debug("포탈 로드됨: " + id);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  public static void onDisable() {
    if (!ENABLED) {
      return;
    }

  }

  public static void onEvent(PlayerMoveEvent event) {
    if (!ENABLED) {
      return;
    }

    Location from = event.getFrom().getBlock().getLocation();
    for (Portal portal : portals.values()) {
      if (portal.trigger(from)) {
        return;
      }
    }

    Location to = event.getTo().getBlock().getLocation();
    for (Portal portal : portals.values()) {
      if (portal.trigger(to)) {
        portal.execute(event.getPlayer());
        return;
      }
    }
  }

  protected static Portal of(String id) {
    return portals.get(id);
  }

}
