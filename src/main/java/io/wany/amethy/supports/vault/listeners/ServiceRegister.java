package io.wany.amethy.supports.vault.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.supports.vault.VaultSupport;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class ServiceRegister implements Listener {

  @EventHandler
  private void onServiceChange(ServiceRegisterEvent event) {
    if (event.getProvider().getService() == Chat.class) {
      if (Amethy.YAMLCONFIG.getBoolean("vault-support.chat.enable")) {
        VaultSupport.CHAT = Bukkit.getServer().getServicesManager().load(Chat.class);
      }
    }
    if (event.getProvider().getService() == Economy.class) {
      if (Amethy.YAMLCONFIG.getBoolean("vault-support.economy.enable")) {
        VaultSupport.ECONOMY = Bukkit.getServer().getServicesManager().load(Economy.class);
      }
    }
    if (event.getProvider().getService() == Permission.class) {
      if (Amethy.YAMLCONFIG.getBoolean("vault-support.economy.enable")) {
        VaultSupport.PERMISSION = Bukkit.getServer().getServicesManager().load(Permission.class);
      }
    }
  }

}
