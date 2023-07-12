package io.wany.amethy.modules.transport;

import io.wany.amethyst.Json;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class HomeData {

  private final UUID uuid;
  private final Json data;
  private final Json storage;

  protected HomeData(UUID uuid, Json storage) {
    this.uuid = uuid;
    this.storage = storage;
    Json data = storage.get(this.uuid.toString());
    this.data = data != null ? data : new Json();
  }

  public void save() {
    this.storage.set(this.uuid.toString(), this.data);
  }

  public void delete() {
    this.storage.delete(this.uuid.toString());
  }

  public Location getLocation() {
    return data != null ? new Location(
      Bukkit.getWorld(this.data.getString("location.world")),
      this.data.getDouble("location.x"),
      this.data.getDouble("location.y"),
      this.data.getDouble("location.z"),
      this.data.getFloat("location.yaw"),
      this.data.getFloat("location.pitch")) : null;
  }

  public void setLocation(Location loc) {
    this.data.set("location.world", loc.getWorld().getName());
    this.data.set("location.x", loc.getX());
    this.data.set("location.y", loc.getY());
    this.data.set("location.z", loc.getZ());
    this.data.set("location.yaw", loc.getYaw());
    this.data.set("location.pitch", loc.getPitch());
  }

  public boolean hasLocation() {
    return this.data != null && this.data.get("location") != null;
  }

}
