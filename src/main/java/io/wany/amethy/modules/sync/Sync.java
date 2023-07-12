package io.wany.amethy.modules.sync;

import io.wany.amethy.console;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethy.supports.essentials.EssentialsSupport;
import io.wany.amethy.supports.vault.VaultSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.wany.amethy.Amethy;
import io.wany.amethy.modules.database.Database;

public class Sync {

  protected static final String prefix = "§l[동기화]:§r ";

  private static boolean enabled = false;
  private static SyncChat syncChat;
  private static SyncConnection syncConnection;
  private static SyncPlayer syncPlayer;
  private static SyncVaultEconomy syncVaultEconomy;

  public static SyncChat getChat() {
    return syncChat;
  }

  public static SyncConnection getConnection() {
    return syncConnection;
  }

  public static SyncPlayer getPlayer() {
    return syncPlayer;
  }

  public static SyncVaultEconomy getVaultEconomy() {
    return syncVaultEconomy;
  }

  public static void onEnable() {
    try {
      if (!Amethy.YAMLCONFIG.getBoolean("sync.enable")) {
        console.debug(prefix + "동기화 §c비활성화됨");
        return;
      }

      if (!Database.ENABLED) {
        console.warn(prefix + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
        console.debug(prefix + "동기화 §c비활성화됨");
        return;
      }

      enabled = true;
      console.debug(prefix + "동기화 §a활성화됨");

      console.debug(Sync.prefix + "플레이어 채팅 동기화 " + (onEnableSyncChat() ? "§a" : "§c비") + "활성화됨");

      console.debug(Sync.prefix + "플레이어 연결 동기화 " + (onEnableSyncConnection() ? "§a" : "§c비") + "활성화됨");

      console.debug(Sync.prefix + "플레이어 정보 동기화 " + (onEnableSyncPlayer() ? "§a" : "§c비") + "활성화됨");

      console.debug(Sync.prefix + "Vault Economy 동기화 " + (onEnableSyncVaultEconomy() ? "§a" : "§c비") + "활성화됨");

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static boolean onEnableSyncChat() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.chat.enable")) {
      return false;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      console.warn(Sync.prefix + "데이터베이스 동기화 이벤트가 비활성화되어 있습니다. 기능이 비활성화됩니다.");
      return false;
    }

    syncChat = new SyncChat();
    syncChat.onEnable();

    return true;
  }

  private static boolean onEnableSyncConnection() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.connection.enable")) {
      return false;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      console.warn(Sync.prefix + "데이터베이스 동기화 이벤트가 비활성화되어 있습니다. 기능이 비활성화됩니다.");
      return false;
    }

    syncConnection = new SyncConnection();
    syncConnection.onEnable();

    return true;
  }

  private static boolean onEnableSyncPlayer() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.player.enable")) {
      return false;
    }

    if (!DatabaseSyncMap.ENABLED) {
      console.warn(Sync.prefix + "데이터베이스 동기화 맵이 비활성화되어 있습니다. 기능이 비활성화됩니다.");
      return false;
    }

    syncPlayer = new SyncPlayer();
    syncPlayer.onEnable();

    return true;
  }

  private static boolean onEnableSyncVaultEconomy() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.vault.economy.enable")) {
      return false;
    }

    if (!VaultSupport.isEnabled()) {
      console.warn(Sync.prefix + "Vault 플러그인 연동을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return false;
    }

    if (!EssentialsSupport.isEnabled()) {
      console.warn(Sync.prefix + "Essentials 플러그인 연동을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return false;
    }

    if (!DatabaseSyncMap.ENABLED) {
      console.warn(Sync.prefix + "데이터베이스 동기화 맵이 비활성화되어 있습니다. 기능이 비활성화됩니다.");
      return false;
    }

    if (!DatabaseSyncEvent.ENABLED) {
      console.warn(Sync.prefix + "데이터베이스 동기화 이벤트가 비활성화되어 있습니다. 기능이 비활성화됩니다.");
      return false;
    }

    syncVaultEconomy = new SyncVaultEconomy();
    syncVaultEconomy.onEnable();

    return true;
  }

  public static void onDisable() {
    if (syncPlayer != null) {
      syncPlayer.onDisable();
    }

    if (syncVaultEconomy != null) {
      syncVaultEconomy.onDisable();
    }
  }

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!enabled) {
      return;
    }

    if (syncPlayer != null) {
      syncPlayer.onPlayerJoin(event);
    }

    if (syncVaultEconomy != null) {
      syncVaultEconomy.onPlayerJoin(event);
    }
  }

  public static void onPlayerQuit(PlayerQuitEvent event) {
    if (!enabled) {
      return;
    }

    if (syncPlayer != null) {
      syncPlayer.onPlayerQuit(event);
    }

    if (syncVaultEconomy != null) {
      syncVaultEconomy.onPlayerQuit(event);
    }
  }

  public static void onPlayerChat(AsyncChatEvent event) {
    if (!enabled) {
      return;
    }

    if (syncChat != null) {
      syncChat.onPlayerChat(event);
    }
  }

  @SuppressWarnings("deprecation") // Spigot API
  public static void onPlayerChat(AsyncPlayerChatEvent event) {
    if (!enabled) {
      return;
    }

    if (syncChat != null) {
      syncChat.onPlayerChat(event);
    }
  }

  public static void onPlayerInteract(PlayerInteractEvent event) {
    if (!enabled) {
      return;
    }

    if (syncPlayer != null) {
      syncPlayer.onPlayerInteract(event);
    }
  }

  public static void onPlayerMove(PlayerMoveEvent event) {
    if (!enabled) {
      return;
    }

    if (syncPlayer != null) {
      syncPlayer.onPlayerMove(event);
    }
  }

  public static void onPlayerDropItem(PlayerDropItemEvent event) {
    if (!enabled) {
      return;
    }

    if (syncPlayer != null) {
      syncPlayer.onPlayerDropItem(event);
    }
  }

  public static void onUserBalanceUpdte(Player player, double balance) {
    if (!enabled) {
      return;
    }

    if (syncVaultEconomy != null) {
      syncVaultEconomy.onUserBalanceUpdate(player, balance);
    }
  }

}
