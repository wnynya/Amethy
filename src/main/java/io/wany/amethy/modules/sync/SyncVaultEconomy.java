package io.wany.amethy.modules.sync;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethy.console;
import io.wany.amethy.supports.essentials.EssentialsSupport;
import io.wany.amethy.supports.vault.VaultSupport;
import io.wany.amethyst.Json;

public class SyncVaultEconomy {

  public static boolean ENABLED = false;

  public static void onUserBalanceUpdte(Player player, double balance) {
    if (!ENABLED) {
      return;
    }

    UUID uuid = player.getUniqueId();

    Json data = new Json();
    data.set("uuid", uuid.toString());
    data.set("balance", balance);

    DatabaseSyncMap.set("sync.vault.economy." + uuid.toString(), data);
    DatabaseSyncEvent.emit("sync/vault/economy", data);
  }

  public static void databaseUserBalanceUpdate(DatabaseSyncEvent event) {
    Json data = event.getValue();

    UUID uuid = UUID.fromString(data.getString("uuid"));
    double balance = data.getDouble("balance");

    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

    double current = VaultSupport.ECONOMY.getBalance(player);

    if (current == balance) {
      return;
    }

    if (current < balance) {
      VaultSupport.ECONOMY.depositPlayer(player, balance - current);
    } else {
      VaultSupport.ECONOMY.withdrawPlayer(player, current - balance);
    }
  }

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    Json data = DatabaseSyncMap.get("sync.vault.economy." + uuid.toString());

    if (data == null) {
      return;
    }

    double balance = data.getDouble("balance");
    double current = VaultSupport.ECONOMY.getBalance(player);

    if (current < balance) {
      VaultSupport.ECONOMY.depositPlayer(player, balance - current);
    } else {
      VaultSupport.ECONOMY.withdrawPlayer(player, current - balance);
    }
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("sync.vault.economy.enable")) {
      console.debug(Sync.PREFIX + "Vault Economy 동기화 §c비활성화됨");
      return;
    }

    if (!VaultSupport.isEnabled()) {
      console.warn(Sync.PREFIX + "Vault 플러그인 연동을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(Sync.PREFIX + "Vault Economy 동기화 §c비활성화됨");
      return;
    }

    if (!EssentialsSupport.isEnabled()) {
      console.warn(Sync.PREFIX + "Essentials 플러그인 연동을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(Sync.PREFIX + "Vault Economy 동기화 §c비활성화됨");
      return;
    }

    if (!DatabaseSyncMap.ENABLED || !DatabaseSyncEvent.ENABLED) {
      console.warn(Sync.PREFIX + "데이터베이스 연결을 확인할 수 없습니다. 기능이 비활성화됩니다.");
      console.debug(Sync.PREFIX + "Vault Economy 동기화 §c비활성화됨");
      return;
    }

    ENABLED = true;
    console.debug(Sync.PREFIX + "Vault Economy 동기화 §a활성화됨");

    DatabaseSyncEvent.on("sync/vault/economy", (args) -> {
      databaseUserBalanceUpdate((DatabaseSyncEvent) args[0]);
    });
  }

  public static void onDisable() {

  }

}
