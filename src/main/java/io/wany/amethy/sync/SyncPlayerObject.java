package io.wany.amethy.sync;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modulesmc.Console;

public interface SyncPlayerObject {

  public String NAMESPACE();

  public String NAME();

  public default boolean ENABLED() {
    return Amethy.YAMLCONFIG.getBoolean("sync." + NAMESPACE());
  }

  public void select(Player player);

  public void update(Player player);

  public default void onPlayerPreJoin(PlayerJoinEvent event) {
  }

  public default void onPlayerJoin(PlayerJoinEvent event) {
    select(event.getPlayer());
  }

  public default void onPlayerQuit(PlayerQuitEvent event) {
    update(event.getPlayer());
  }

  public default void onDisable(Player player) {
    update(player);
  }

  public default void onEnable() {
    Console.debug(Sync.PREFIX + "플레이어 정보 동기화 - " + NAME() + " " + (ENABLED() ? "&a" : "&c비") + "활성화됨");
  }

  public default void onDisable() {
  }

}
