package io.wany.amethy.modules.sync;

import io.wany.amethy.console;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;

public interface SyncPlayerObject {

  String NAMESPACE();

  String NAME();

  default boolean ENABLED() {
    return Amethy.YAMLCONFIG.getBoolean("sync." + NAMESPACE());
  }

  void select(Player player);

  void update(Player player);

  default void onPlayerPreJoin(PlayerJoinEvent event) {
  }

  default void onPlayerJoin(PlayerJoinEvent event) {
    select(event.getPlayer());
  }

  default void onPlayerQuit(PlayerQuitEvent event) {
    update(event.getPlayer());
  }

  default void onDisable(Player player) {
    update(player);
  }

  default void onEnable() {
    console.debug(Sync.PREFIX + "플레이어 정보 동기화 - " + NAME() + " " + (ENABLED() ? "§a" : "§c비") + "활성화됨");
  }

  default void onDisable() {
  }

}
