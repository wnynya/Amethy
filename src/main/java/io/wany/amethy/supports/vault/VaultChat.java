package io.wany.amethy.supports.vault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class VaultChat {

  public static String prefix(Player player) {
    return VaultSupport.isEnabled() ? VaultSupport.CHAT.getPlayerPrefix(player) : "";
  }

  public static String suffix(Player player) {
    return VaultSupport.isEnabled() ? VaultSupport.CHAT.getPlayerSuffix(player) : "";
  }

  public static String prefix(OfflinePlayer player) {
    World world = Bukkit.getWorlds().get(0);
    return VaultSupport.isEnabled() ? VaultSupport.CHAT.getPlayerPrefix(String.valueOf(world), player) : "";
  }

  public static String suffix(OfflinePlayer player) {
    World world = Bukkit.getWorlds().get(0);
    return VaultSupport.isEnabled() ? VaultSupport.CHAT.getPlayerSuffix(String.valueOf(world), player) : "";
  }

}
