package io.wany.amethy.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modulesmc.Console;
import io.wany.amethy.modulesmc.Message;

public class SyncPlayer {

  private static List<SyncPlayerObject> syncPlayerObjects = new ArrayList<>();

  // 플레이어 체력 동기화
  private static SyncPlayerObject syncPlayerHealth = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.health";
    }

    @Override
    public String NAME() {
      return "체력";
    }

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
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

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      JsonObject object = new JsonObject();
      object.addProperty("health", player.getHealth());
      object.addProperty("healthscale", player.getHealthScale());
      object.addProperty("foodlevel", player.getFoodLevel());
      object.addProperty("exhaution", player.getExhaustion());
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          object.toString());
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerHealth);
  }

  // 플레이어 경험치 (레벨) 동기화
  private static SyncPlayerObject syncPlayerExperience = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.experience";
    }

    @Override
    public String NAME() {
      return "경험치";
    }

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString());
      if (value == null) {
        return;
      }
      JsonObject object = JsonParser.parseString(value).getAsJsonObject();
      player.setExp(object.get("exp").getAsFloat());
      player.setLevel(object.get("level").getAsInt());
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      JsonObject object = new JsonObject();
      object.addProperty("exp", player.getExp());
      object.addProperty("level", player.getLevel());
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          object.toString());
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerExperience);
  }

  // 플레이어 인벤토리 동기화
  private static SyncPlayerObject syncPlayerInventory = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.inventory";
    }

    @Override
    public String NAME() {
      return "인벤토리";
    }

    public HashMap<UUID, JsonArray> inventories = new HashMap<>();

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
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

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      JsonArray array = JsonInventory.jsonify(player.getInventory());
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          array.toString());
    }

    @Override
    public void onPlayerPreJoin(PlayerJoinEvent event) {
      if (!ENABLED()) {
        return;
      }
      Player player = event.getPlayer();
      inventories.put(player.getUniqueId(), JsonInventory.jsonify(player.getInventory()));
      player.getInventory().setContents(new ItemStack[0]);
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerInventory);
  }

  // 플레이어 엔더 상자 동기화
  private static SyncPlayerObject syncPlayerEnderChest = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.enderchest";
    }

    @Override
    public String NAME() {
      return "엔더 상자";
    }

    public HashMap<UUID, JsonArray> inventories = new HashMap<>();

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString());
      if (value == null) {
        return;
      }
      JsonArray array = JsonParser.parseString(value).getAsJsonArray();
      JsonInventory.apply(array, player.getEnderChest(), player);
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      JsonArray array = JsonInventory.jsonify(player.getEnderChest());
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          array.toString());
    }

    @Override
    public void onPlayerPreJoin(PlayerJoinEvent event) {
      if (!ENABLED()) {
        return;
      }

      Player player = event.getPlayer();
      inventories.put(player.getUniqueId(), JsonInventory.jsonify(player.getInventory()));
      player.getInventory().setContents(new ItemStack[0]);
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerInventory);
  }

  // 플레이어 포션 효과 (이펙트) 동기화
  private static SyncPlayerObject syncPlayerEffect = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.effects";
    }

    @Override
    public String NAME() {
      return "표션 효과";
    }

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString());
      if (value == null) {
        return;
      }
      JsonArray array = JsonParser.parseString(value).getAsJsonArray();
      JsonPotionEffects.apply(array, player);
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      JsonArray array = JsonPotionEffects.jsonify(player);
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          array.toString());
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerEffect);
  }

  // 큐컴버리 커스텀 이펙트 동기화
  private static SyncPlayerObject syncPlayerCucumberyCustomEffect = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.cucumberycustomeffects";
    }

    @Override
    public String NAME() {
      return "큐컴버리 커스텀 이펙트";
    }

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String value = Sync.get(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString());
      if (value == null) {
        return;
      }
      StringCucumberyEffects.apply(value, player);
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      String string = StringCucumberyEffects.stringify(player);
      Sync.set(
          NAMESPACE(),
          SyncPlayer.CHANNEL + "." + uuid.toString(),
          string);
    }

  };
  static {
    syncPlayerObjects.add(syncPlayerEffect);
  }

  protected static boolean ENABLED = false;
  protected static String CHANNEL = "";

  private static List<UUID> kicked = new ArrayList<>();

  protected static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();

    syncPlayerInventory.onPlayerPreJoin(event);

    Bukkit.getScheduler().runTaskLater(Amethy.PLUGIN, () -> {

      UUID uuid = player.getUniqueId();
      String isonline = Sync.get(
          "player.isonline",
          CHANNEL + "." + uuid.toString());
      if (isonline != null && isonline.equals("true")) {
        kicked.add(uuid);
        player.kick(Message.of("플레이어 정보 동기화 중 오류가 발생하였습니다. 서버에 다시 접속하여 주십시오."));
        return;
      }

      updateOnline(player, true);

      for (SyncPlayerObject object : syncPlayerObjects) {
        object.onPlayerJoin(event);
      }
    }, 20L);
  }

  protected static void onPlayerQuit(PlayerQuitEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    updateOnline(player, false);

    UUID uuid = player.getUniqueId();
    if (kicked.contains(uuid)) {
      kicked.remove(uuid);
      return;
    }

    for (SyncPlayerObject object : syncPlayerObjects) {
      object.onPlayerQuit(event);
    }
  }

  private static void updateOnline(Player player, boolean isonline) {
    if (!ENABLED) {
      return;
    }

    UUID uuid = player.getUniqueId();
    Sync.set(
        "player.isonline",
        CHANNEL + "." + uuid.toString(),
        isonline + "");
  }

  protected static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.player.enable")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 &c비활성화됨");
      return;
    }

    CHANNEL = Amethy.YAMLCONFIG.getString("sync.player.channel");
    CHANNEL = CHANNEL.replaceAll("[^a-z0-9_-]", "");
    if (CHANNEL.length() <= 0) {
      Console.warn(Sync.PREFIX + "플레이어 정보 동기화 채널 값이 잘못 설정되었거나 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return;
    }
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 채널: " + CHANNEL);

    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 &a활성화됨");
    for (SyncPlayerObject object : syncPlayerObjects) {
      object.onEnable();
    }
  }

  protected static void onDisable() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      updateOnline(player, false);
      for (SyncPlayerObject object : syncPlayerObjects) {
        object.onDisable(player);
      }
    }
    for (SyncPlayerObject object : syncPlayerObjects) {
      object.onDisable();
    }
  }

}
