package io.wany.amethy.sync;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncPlayerInventory {

  public static boolean ENABLED = false;
  private static final String NAMESPACE = "player.inventory";

  public static HashMap<UUID, JsonArray> inventories = new HashMap<>();

  public static void onPlayerJoinPre(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    inventories.put(player.getUniqueId(), JsonInventory.jsonify(player.getInventory()));
    player.getInventory().setContents(new ItemStack[0]);
  }

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    String value = Sync.get(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString());

    JsonArray array;
    if (value == null) {
      array = inventories.get(uuid);
    } else {
      array = JsonParser.parseString(value).getAsJsonArray();
    }
    inventories.remove(uuid);

    JsonInventory.apply(array, player.getInventory(), player);
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
    JsonArray array = JsonInventory.jsonify(player.getInventory());
    Sync.set(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString(),
        array.toString());
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.player.inventory")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 인벤토리 &c비활성화됨");
      return;
    }
    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 인벤토리 &a활성화됨");
  }

  public static void onDisable() {

  }

}
