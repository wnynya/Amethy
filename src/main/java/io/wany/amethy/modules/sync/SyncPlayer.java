package io.wany.amethy.modules.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethy.console;
import io.wany.amethy.modulesmc.Message;
import io.wany.amethy.supports.cucumbery.CucumberySupport;
import io.wany.amethy.supports.cucumbery.sync.SyncCucumberyPlayer;
import io.wany.amethyst.Json;

public class SyncPlayer {

  public static List<SyncPlayerObject> OBJECTS = new ArrayList<>();

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
      Json data = DatabaseSyncMap.get("sync." + NAMESPACE() + "." + uuid.toString());
      if (data == null) {
        return;
      }
      double health = Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
          Math.max(0, data.getDouble("health")));
      int foodlevel = (int) Math.min(20, Math.max(0, data.getInt("foodlevel")));
      float exhaution = (float) Math.min(20, Math.max(0, data.getFloat("exhaution")));
      player.setHealth(health);
      player.setHealthScale(data.getDouble("healthscale"));
      player.setFoodLevel(foodlevel);
      player.setExhaustion(exhaution);
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      Json data = new Json();
      data.set("health", player.getHealth());
      data.set("healthscale", player.getHealthScale());
      data.set("foodlevel", player.getFoodLevel());
      data.set("exhaution", player.getExhaustion());
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), data);
    }

  };
  static {
    OBJECTS.add(syncPlayerHealth);
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
      Json data = DatabaseSyncMap.get("sync." + NAMESPACE() + "." + uuid.toString());
      if (data == null) {
        return;
      }
      player.setExp(data.getFloat("exp"));
      player.setLevel(data.getInt("level"));
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      Json data = new Json();
      data.set("exp", player.getExp());
      data.set("level", player.getLevel());
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), data);
    }

  };
  static {
    OBJECTS.add(syncPlayerExperience);
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
      String value = DatabaseSyncMap.getString("sync." + NAMESPACE() + "." + uuid.toString());
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
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid, array.toString());
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
    OBJECTS.add(syncPlayerInventory);
  }

  // 플레이어 인벤토리 포지션 동기화
  private static SyncPlayerObject syncPlayerInventorySlot = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.inventoryslot";
    }

    @Override
    public String NAME() {
      return "인벤토리 슬롯";
    }

    @Override
    public void select(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      Json data = DatabaseSyncMap.get("sync." + NAMESPACE() + "." + uuid.toString());
      if (data == null) {
        return;
      }
      int slot = data.getInt("slot");
      player.getInventory().setHeldItemSlot(slot);
    }

    @Override
    public void update(Player player) {
      if (!ENABLED()) {
        return;
      }
      UUID uuid = player.getUniqueId();
      int slot = player.getInventory().getHeldItemSlot();
      Json data = new Json();
      data.set("slot", slot);
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid, data);
    }

  };
  static {
    OBJECTS.add(syncPlayerInventorySlot);
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
      String value = DatabaseSyncMap.getString("sync." + NAMESPACE() + "." + uuid.toString());
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
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), array.toString());
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
    OBJECTS.add(syncPlayerEnderChest);
  }

  // 플레이어 포션 효과 (이펙트) 동기화
  private static SyncPlayerObject syncPlayerEffect = new SyncPlayerObject() {

    @Override
    public String NAMESPACE() {
      return "player.potioneffects";
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
      String value = DatabaseSyncMap.getString("sync." + NAMESPACE() + "." + uuid.toString());
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
      DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), array.toString());
    }

  };
  static {
    OBJECTS.add(syncPlayerEffect);
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
      String isonline = DatabaseSyncMap.getString("sync." + "player.isonline" + "." + uuid.toString());
      if (isonline != null && isonline.equals("true")) {
        kicked.add(uuid);
        player.kick(Message.of("플레이어 정보 동기화 중 오류가 발생하였습니다. 서버에 다시 접속하여 주십시오."));
        return;
      }

      updateOnline(player, true);

      for (SyncPlayerObject object : OBJECTS) {
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

    for (SyncPlayerObject object : OBJECTS) {
      object.onPlayerQuit(event);
    }
  }

  private static void updateOnline(Player player, boolean isonline) {
    if (!ENABLED) {
      return;
    }

    UUID uuid = player.getUniqueId();
    DatabaseSyncMap.set("sync." + "player.isonline" + "." + uuid.toString(), isonline + "");
  }

  private static ExecutorService onEnableExecutor = Executors.newFixedThreadPool(1);
  private static Timer onEnableTimer = new Timer();

  protected static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.player.enable")) {
      console.debug(Sync.PREFIX + "플레이어 정보 동기화 §c비활성화됨");
      return;
    }

    if (!DatabaseSyncMap.ENABLED) {
      console.warn(Sync.PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(Sync.PREFIX + "플레이어 정보 동기화 §c비활성화됨");
      return;
    }

    ENABLED = true;
    console.debug(Sync.PREFIX + "플레이어 정보 동기화 §a활성화됨");

    if (CucumberySupport.isEnabled()) {
      SyncCucumberyPlayer.onEnable();
    }

    for (SyncPlayerObject object : OBJECTS) {
      object.onEnable();
    }

    onEnableExecutor.submit(() -> {
      onEnableTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          for (Player player : Bukkit.getOnlinePlayers()) {
            for (SyncPlayerObject object : OBJECTS) {
              object.update(player);
            }
          }
        }
      }, 0, 1000 * 60);
    });
  }

  protected static void onDisable() {
    onEnableTimer.cancel();
    onEnableExecutor.shutdown();

    for (Player player : Bukkit.getOnlinePlayers()) {
      updateOnline(player, false);
      for (SyncPlayerObject object : OBJECTS) {
        object.onDisable(player);
      }
    }

    for (SyncPlayerObject object : OBJECTS) {
      object.onDisable();
    }
  }

}
