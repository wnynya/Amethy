package io.wany.amethy.modules.transport;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.wany.amethy.Amethy;
import io.wany.amethyst.Json;

public class Home {

  protected static boolean ENABLED = false;
  protected static final String PREFIX = Amethy.COLOR + "§l[홈]: §r";

  private Json data = new Json();
  private UUID uuid;

  Home(Player player) {
    this.uuid = player.getUniqueId();
    Location loc = player.getLocation();
    this.data.set("name", player.getName() + "의 집");
    this.data.set("desc", "");
    this.data.set("visit", 0);
    this.data.set("vote", 0);
    this.data.set("location.world", loc.getWorld().getName());
    this.data.set("location.x", loc.getX());
    this.data.set("location.y", loc.getY());
    this.data.set("location.z", loc.getZ());
    this.data.set("location.yaw", loc.getYaw());
    this.data.set("location.pitch", loc.getPitch());
  }

  private Home(UUID uuid) {
    this.uuid = uuid;
    this.data = storage.get(uuid.toString());
  }

  void save() {
    storage.set(this.uuid.toString(), this.data);
  }

  void delete() {
    storage.delete(this.uuid.toString());
  }

  Location getLocation() {
    return new Location(
        Bukkit.getWorld(this.data.getString("location.world")),
        this.data.getDouble("location.x"),
        this.data.getDouble("location.y"),
        this.data.getDouble("location.z"),
        this.data.getFloat("location.yaw"),
        this.data.getFloat("location.pitch"));
  }

  static Home of(UUID uuid) {
    Home home = new Home(uuid);
    return home.data == null ? null : home;
  }

  private static Json storage;

  public static void onEnable() {
    try {
      storage = new Json(Amethy.PLUGIN_DIR.toPath().resolve("transport/home.json").toFile());
      storage.save();

      Amethy.PLUGIN.registerCommand("home", new HomeCommand());

      if (!Amethy.YAMLCONFIG.getBoolean("transport.home.enable")) {
        return;
      }

      ENABLED = true;
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
