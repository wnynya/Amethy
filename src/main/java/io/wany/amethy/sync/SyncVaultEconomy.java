package io.wany.amethy.sync;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncVaultEconomy {

  public static boolean ENABLED = false;
  public static String CHANNEL = "";

  private static final String NAMESPACE = "vaulteconomy";

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!ENABLED) {
      return;
    }

  }

  public static void onPlayerQuit(PlayerQuitEvent event) {
    if (!ENABLED) {
      return;
    }

  }

  public static void update(Player player) {

  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.vault.economy.enable")) {
      Console.debug(Sync.PREFIX + "Vault Economy 동기화 &c비활성화됨");
      return;
    }

    CHANNEL = Amethy.CONFIG.getString("sync.vault.economy.channel");
    CHANNEL = CHANNEL.replaceAll("[^a-z0-9_-]", "");
    if (CHANNEL.length() <= 0) {
      Console.warn(Sync.PREFIX + "Vault Economy 동기화 채널 값이 잘못 설정되었거나 확인할 수 없습니다. 기능이 비활성화됩니다.");
      return;
    }
    Console.debug(Sync.PREFIX + "Vault Economy 동기화 채널: " + CHANNEL);

    ENABLED = true;
    Console.debug(Sync.PREFIX + "Vault Economy 동기화 &a활성화됨");
  }

  public static void onDisable() {

  }

}
