package io.wany.amethy.sync;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncPlayerEnderChest {

  public static boolean ENABLED = false;
  private static final String NAMESPACE = "player.enderchest";

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
    JsonArray array = JsonParser.parseString(value).getAsJsonArray();

    JsonInventory.apply(array, player.getEnderChest(), player);
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
    JsonArray array = JsonInventory.jsonify(player.getEnderChest());
    Sync.set(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString(),
        array.toString());
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.player.enderchest")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 엔더 상자 &c비활성화됨");
      return;
    }
    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 엔더 상자 &a활성화됨");
  }

  public static void onDisable() {

  }

}
