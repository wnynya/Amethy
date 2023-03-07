package io.wany.amethy.supports.vault;

import org.bukkit.Bukkit;

import io.wany.amethy.Amethy;
import io.wany.amethy.supports.PluginSupport;
import io.wany.amethy.supports.vault.listeners.ServiceRegister;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultSupport {

  public static PluginSupport SUPPORT;
  public static Chat CHAT;
  public static Economy ECONOMY;
  public static Permission PERMISSION;

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("vault-support.enable")) {
      return;
    }

    SUPPORT = new PluginSupport("Vault");
    SUPPORT.on("enable", (args) -> {
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
