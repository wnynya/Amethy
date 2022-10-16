package io.wany.amethy.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.Message;

public class SyncPlayer {

  protected static boolean ENABLED = false;
  protected static String CHANNEL = "";

  private static List<UUID> kicked = new ArrayList<>();

  protected static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    SyncPlayerInventory.onPlayerJoinPre(event);

    Bukkit.getScheduler().runTaskLater(Amethy.PLUGIN, () -> {
      String isonline = Sync.get(
          "player.isonline",
          CHANNEL + "." + uuid.toString());
      if (isonline != null && isonline.equals("true")) {
        kicked.add(uuid);
        player.kick(Message.of("플레이어 정보 동기화 중 오류가 발생하였습니다. 서버에 재 접속하여 주십시오."));
        return;
      }
      updateOnline(player, true);

      SyncPlayerHealth.onPlayerJoin(event);
      SyncPlayerExperience.onPlayerJoin(event);
      SyncPlayerInventory.onPlayerJoin(event);
      SyncPlayerEnderChest.onPlayerJoin(event);
      SyncPlayerPotionEffects.onPlayerJoin(event);
      SyncVaultEconomy.onPlayerJoin(event);
      SyncPlayerCucumberyCustomEffects.onPlayerJoin(event);
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

    SyncPlayerHealth.onPlayerQuit(event);
    SyncPlayerExperience.onPlayerQuit(event);
    SyncPlayerInventory.onPlayerQuit(event);
    SyncPlayerEnderChest.onPlayerQuit(event);
    SyncPlayerPotionEffects.onPlayerQuit(event);
    SyncPlayerCucumberyCustomEffects.onPlayerQuit(event);
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
    if (!Amethy.CONFIG.getBoolean("sync.player.enable")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 &c비활성화됨");
      return;
    }

    CHANNEL = Amethy.CONFIG.getString("sync.player.channel");
    CHANNEL = CHANNEL.replaceAll("[^a-z0-9_-]", "");
    if (CHANNEL.length() <= 0) {
      Console.warn(Sync.PREFIX + "플레이어 정보 동기화 채널 값이 잘못 설정되었거나 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return;
    }
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 채널: " + CHANNEL);

    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 &a활성화됨");

    SyncPlayerHealth.onEnable();
    SyncPlayerExperience.onEnable();
    SyncPlayerInventory.onEnable();
    SyncPlayerEnderChest.onEnable();
    SyncPlayerPotionEffects.onEnable();
    SyncPlayerCucumberyCustomEffects.onEnable();
  }

  protected static void onDisable() {

  }

}
