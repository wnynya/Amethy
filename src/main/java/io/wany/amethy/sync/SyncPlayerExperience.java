package io.wany.amethy.sync;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncPlayerExperience {

  public static boolean ENABLED = false;
  private static final String NAMESPACE = "player.experience";

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
    player.setExp(object.get("exp").getAsFloat());
    player.setLevel(object.get("level").getAsInt());
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
    object.addProperty("exp", player.getExp());
    object.addProperty("level", player.getLevel());
    Sync.set(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString(),
        object.toString());
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.player.experience")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 경험치 &c비활성화됨");
      return;
    }
    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 경험치 &a활성화됨");
  }

  public static void onDisable() {

  }

}
