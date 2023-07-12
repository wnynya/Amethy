package io.wany.amethy.modules.sync;

import io.wany.amethy.console;
import io.wany.amethy.modules.database.DatabaseSyncEvent;
import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethy.supports.vault.VaultSupport;
import io.wany.amethyst.Json;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncVaultEconomy {

  private boolean enabled = false;
  private final ExecutorService onEnableService = Executors.newFixedThreadPool(1);
  private final Timer onEnableTimer = new Timer();

  public boolean isEnabled() {
    return this.enabled;
  }

  private Double select(Player player) {
    UUID uuid = player.getUniqueId();
    Json data = DatabaseSyncMap.get("sync/vault/economy$" + uuid);
    return data != null ? data.getDouble("balance") : null;
  }

  private void update(Player player) {
    UUID uuid = player.getUniqueId();
    Json data = new Json();
    data.set("balance", VaultSupport.ECONOMY.getBalance(player));
    DatabaseSyncMap.set("ssync/vault/economy$" + uuid, data);
  }

  private void setBalance(OfflinePlayer player, double balance) {
    double current = VaultSupport.ECONOMY.getBalance(player);

    if (current < balance) {
      VaultSupport.ECONOMY.depositPlayer(player, balance - current);
    } else {
      VaultSupport.ECONOMY.withdrawPlayer(player, current - balance);
    }
  }

  protected void onEnable() {
    this.enabled = true;

    DatabaseSyncEvent.on("sync/vault/economy", (args) -> {
      onDatabaseBalanceUpdate((DatabaseSyncEvent) args[0]);
    });

    onEnableService.submit(() -> {
      onEnableTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
          }
        }
      }, 0, 1000 * 2);
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
      update(player);
    }
  }

  protected void onPlayerJoin(PlayerJoinEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();

    Double balance = select(player);

    if (balance != null) {
      setBalance(player, balance);
    }
  }

  protected void onPlayerQuit(PlayerQuitEvent event) {
    if (!this.enabled) {
      return;
    }

    Player player = event.getPlayer();

    update(player);
  }

  protected void onUserBalanceUpdate(Player player, double balance) {
    if (!this.enabled) {
      return;
    }

    if (VaultSupport.ECONOMY.getBalance(player) == balance) {
      return;
    }

    update(player);

    UUID uuid = player.getUniqueId();

    Json data = new Json();
    data.set("uuid", uuid.toString());
    data.set("balance", balance);

    DatabaseSyncEvent.emit("sync/vault/economy", data);

    console.debug("VaultECO: [OUT] " + uuid + " : " + balance);
  }

  private void onDatabaseBalanceUpdate(DatabaseSyncEvent event) {
    if (!this.enabled) {
      return;
    }

    Json data = event.getValue();

    UUID uuid = UUID.fromString(data.getString("uuid"));
    double balance = data.getDouble("balance");

    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

    setBalance(player, balance);

    console.debug("VaultECO: [IN] " + uuid + " : " + balance);
  }

}
