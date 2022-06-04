package io.wany.amethy.terminal;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Message;

public class TerminalPlayers {

  private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private static BukkitTask bukkitTask1t;
  private static final Timer timer1s = new Timer();

  public static void sendPlayers() {
    try {
      JsonArray array = new JsonArray();
      for (Player player : Bukkit.getOnlinePlayers()) {
        JsonObject object = new JsonObject();
        object.addProperty("name", player.getName());
        object.addProperty("uuid", player.getUniqueId().toString());
        object.addProperty("displayName", Message.stringify(player.displayName()));
        object.addProperty("gamemode", player.getGameMode().name());
        object.addProperty("op", player.isOp());
        object.addProperty("ping", player.getPing());
        array.add(object);
      }
      Terminal.event("players", array);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void sendPlayer(String client, String uuidString) {
    JsonObject data = new JsonObject();
    data.addProperty("client", client);

    JsonObject object = new JsonObject();
    UUID uuid;
    try {
      uuid = UUID.fromString(uuidString);
    } catch (Exception ignored) {
      return;
    }

    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      object.addProperty("uuid", uuid.toString());
      object.addProperty("offline", true);
      data.add("data", object);
      Terminal.event("players-target", data);
      return;
    }

    object.addProperty("name", player.getName());
    object.addProperty("uuid", player.getUniqueId().toString());
    object.addProperty("displayName", Message.stringify(player.displayName()));
    object.addProperty("gamemode", player.getGameMode().name());
    object.addProperty("op", player.isOp());
    object.addProperty("ping", player.getPing());

    try {
      object.addProperty("address", player.getAddress().getHostName());
    } catch (Exception ignored) {
    }

    JsonObject locations = new JsonObject();
    JsonObject currentLocation = new JsonObject();
    Location currentL = player.getLocation();
    currentLocation.addProperty("world", currentL.getWorld().getName());
    currentLocation.addProperty("x", currentL.getX());
    currentLocation.addProperty("y", currentL.getY());
    currentLocation.addProperty("z", currentL.getZ());
    currentLocation.addProperty("yaw", currentL.getYaw());
    currentLocation.addProperty("pitch", currentL.getPitch());
    locations.add("current", currentLocation);
    JsonObject bedLocation = new JsonObject();
    Location bedL = player.getBedSpawnLocation() != null ? player.getBedSpawnLocation()
        : player.getWorld().getSpawnLocation();
    bedLocation.addProperty("world", bedL.getWorld().getName());
    bedLocation.addProperty("x", bedL.getX());
    bedLocation.addProperty("y", bedL.getY());
    bedLocation.addProperty("z", bedL.getZ());
    bedLocation.addProperty("yaw", bedL.getYaw());
    bedLocation.addProperty("pitch", bedL.getPitch());
    locations.add("spawn", bedLocation);
    object.add("locations", locations);

    object.addProperty("health", player.getHealth());
    object.addProperty("maxHealth", player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    object.addProperty("hunger", player.getFoodLevel());
    object.addProperty("armor", player.getAttribute(Attribute.GENERIC_ARMOR).getValue());
    object.addProperty("armorTough", player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue());

    data.add("data", object);
    Terminal.event("players-target", data);
  }

  public static void onEnable() {
    executorService.submit(() -> {
      timer1s.schedule(new TimerTask() {
        @Override
        public void run() {
          sendPlayers();
        }
      }, 0, 1000);
    });
  }

  public static void onDisable() {
    timer1s.cancel();
    executorService.shutdownNow();
  }
}
