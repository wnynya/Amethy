package io.wany.amethy.sync;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Console;

public class SyncPlayerCucumberyCustomEffects {

  public static boolean ENABLED = false;
  private static final String NAMESPACE = "player.cucumberycustomeffects";

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

    StringCucumberyEffects.apply(value, player);
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
    String string = StringCucumberyEffects.stringify(player);
    Sync.set(
        NAMESPACE,
        SyncPlayer.CHANNEL + "." + uuid.toString(),
        string);
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("sync.player.cucumberycustomeffects")) {
      Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 큐컴버리 커스텀 이펙트 &c비활성화됨");
      return;
    }
    ENABLED = true;
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - 큐컴버리 커스텀 이펙트 &a활성화됨");
  }

  public static void onDisable() {

  }

}
