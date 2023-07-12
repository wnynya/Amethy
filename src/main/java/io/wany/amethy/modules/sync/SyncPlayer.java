package io.wany.amethy.modules.sync;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethyst.Json;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncPlayer {

  private boolean enabled = false;
  private final List<UUID> pendingPlayers = new ArrayList<>();
  private final ExecutorService onEnableService = Executors.newFixedThreadPool(1);
  private final Timer onEnableTimer = new Timer();

  public boolean isEnabled() {
    return this.enabled;
  }

  private SyncPlayerData select(Player player) {
    UUID uuid = player.getUniqueId();
    Json jsonPlayer = DatabaseSyncMap.get("sync/player$" + uuid);
    return jsonPlayer != null ? new SyncPlayerData(jsonPlayer, player) : null;
  }

  private void updateUnDone(Player player) {
    UUID uuid = player.getUniqueId();
    SyncPlayerData syncPlayerData = new SyncPlayerData(player, false);
    DatabaseSyncMap.set("sync/player$" + uuid, syncPlayerData.jsonify());
  }

  private void updateDone(Player player) {
    UUID uuid = player.getUniqueId();
    SyncPlayerData syncPlayerData = new SyncPlayerData(player, true);
    DatabaseSyncMap.set("sync/player$" + uuid, syncPlayerData.jsonify());
  }

  protected void onEnable() {
    this.enabled = true;

    onEnableService.submit(() -> {
      onEnableTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> {
              updateUnDone(player);
            });
          }
        }
      }, 0, 1000 * 60);
    });
  }

  protected void onDisable() {
    if (!this.enabled) {
      return;
    }

    enabled = false;

    onEnableTimer.cancel();
    onEnableService.shutdown();

    for (Player player : Bukkit.getOnlinePlayers()) {
      updateDone(player);
      pendingPlayers.add(player.getUniqueId());
    }
  }

  protected void onPlayerJoin(PlayerJoinEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    // 타임아웃으로 킥 후 재접속한 경우 (5초간 완료 플래그 확인 실패)
    if (pendingPlayers.contains(uuid)) {
      SyncPlayerData syncPlayerData = select(player);
      if (syncPlayerData != null) {
        Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> {
          // 데이터 적용
          syncPlayerData.apply(player);
          updateUnDone(player);
          pendingPlayers.remove(uuid);
        });
      }
      // 플레이어 접속 기록이 그 사이에 날아간 경우
      else {
        pendingPlayers.remove(uuid);
        updateUnDone(player);
      }
      return;
    }

    pendingPlayers.add(uuid);

    Timer checkTimer = new Timer();
    Timer timeoutTimer = new Timer();

    // 200ms 마다 플레이어 오프라인 처리 확인
    checkTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (!player.isOnline()) {
          checkTimer.cancel();
          timeoutTimer.cancel();
        }
        SyncPlayerData syncPlayerData = select(player);
        // 플레이어 접속 기록이 있는 경우
        if (syncPlayerData != null) {
          // 완료 플래그가 확인된 경우
          if (syncPlayerData.isDone()) {
            Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> {
              // 데이터 적용
              syncPlayerData.apply(player);
              updateUnDone(player);
              pendingPlayers.remove(uuid);
              checkTimer.cancel();
              timeoutTimer.cancel();
            });
          }
        }
        // 플레이어 접속 기록이 없는 경우
        else {
          Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> {
            updateUnDone(player);
            pendingPlayers.remove(uuid);
            checkTimer.cancel();
            timeoutTimer.cancel();
          });
        }
      }
    }, 0, 200);

    // 5000ms 동안 확인 후 타임아웃
    timeoutTimer.schedule(new TimerTask() {
      @Override
      @SuppressWarnings("deprecation") // player.kick(Component component);
      public void run() {
        checkTimer.cancel();
        timeoutTimer.cancel();
        Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> player.kickPlayer("플레이어 정보 동기화 중 오류가 발생하였습니다. 서버에 재접속하시기 바랍니다."));
      }
    }, 1000 * 5);
  }

  protected void onPlayerQuit(PlayerQuitEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    // 데이터 로딩 중 나가면 업데이트 요청 안함
    if (!pendingPlayers.contains(uuid)) {
      Bukkit.getScheduler().runTask(Amethy.PLUGIN, () -> {
        updateDone(player);
      });
    }
  }

  protected void onPlayerInteract(PlayerInteractEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (pendingPlayers.contains(uuid)) {
      event.setCancelled(true);
    }
  }

  protected void onPlayerMove(PlayerMoveEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (pendingPlayers.contains(uuid)) {
      event.setCancelled(true);
    }
  }

  protected void onPlayerDropItem(PlayerDropItemEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (pendingPlayers.contains(uuid)) {
      event.setCancelled(true);
    }
  }

}
