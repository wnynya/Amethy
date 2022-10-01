package io.wany.amethy.sync;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncPlayerHealth {

  public static boolean ENABLED = false;
  private static final String NAMESPACE = "player.health";

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    String value = Sync.get(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString());
    if (value == null) {
      return;
    }
    JsonObject object = JsonParser.parseString(value).getAsJsonObject();
    player.setHealth(object.get("health").getAsDouble());
    player.setHealthScale(object.get("healthscale").getAsDouble());
    player.setFoodLevel(object.get("foodlevel").getAsInt());
    player.setExhaustion(object.get("exhaution").getAsFloat());
  }

  public static void onPlayerQuit(PlayerQuitEvent event) {
    if (!ENABLED) {
      return;
    }

    update(event.getPlayer());
  }

  public static void update(Player player) {
    if (!ENABLED) {
      return;
    }

    UUID uuid = player.getUniqueId();
    JsonObject object = new JsonObject();
    object.addProperty("health", player.getHealth());
    object.addProperty("healthscale", player.getHealthScale());
    object.addProperty("foodlevel", player.getFoodLevel());
    object.addProperty("exhaution", player.getExhaustion());
    Sync.set(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString(),
        object.toString());
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.player.health")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 체력 &c비활성화됨");
      return;
    }
    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 체력 &a활성화됨");
  }

  public static void onDisable() {

  }

}
