package io.wany.amethy.supports.vault;

import io.wany.amethy.console;
import org.bukkit.Bukkit;

import io.wany.amethy.Amethy;
import io.wany.amethy.supports.PluginSupport;
import io.wany.amethy.supports.vault.listeners.ServiceRegister;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultSupport {

  public static PluginSupport SUPPORT;
  public static String PREFIX = "§6§l[Vault 연동]:§r ";

  public static Chat CHAT;
  public static Economy ECONOMY;
  public static Permission PERMISSION;

  public static boolean isEnabled() {
    if (SUPPORT == null) {
      return false;
    } else {
      return SUPPORT.isEnabled();
    }
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("vault-support.enable")) {
      console.debug(PREFIX + "§c비활성화됨");
      return;
    }
    console.debug(PREFIX + "§a활성화됨");

    SUPPORT = new PluginSupport("Vault");
    SUPPORT.on("enable", (args) -> {
      console.debug(PREFIX + "로드됨");
      Amethy.PLUGIN.registerEvent(new ServiceRegister());

      if (Amethy.YAMLCONFIG.getBoolean("vault-support.chat.enable")) {
        VaultSupport.CHAT = Bukkit.getServer().getServicesManager().load(Chat.class);
      }
      if (Amethy.YAMLCONFIG.getBoolean("vault-support.economy.enable")) {
        VaultSupport.ECONOMY = Bukkit.getServer().getServicesManager().load(Economy.class);
      }
      if (Amethy.YAMLCONFIG.getBoolean("vault-support.economy.enable")) {
        VaultSupport.PERMISSION = Bukkit.getServer().getServicesManager().load(Permission.class);
      }
    });
    SUPPORT.ready();
  }

}
