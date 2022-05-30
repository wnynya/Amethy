package io.wany.amethy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.wany.amethy.gui.Menu;

public class MenuCommand implements CommandExecutor {

  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

    if (!sender.hasPermission("amethy.menu")) {
      return true;
    }
    if (!(sender instanceof Player player)) {
      return true;
    }

    Menu.show(player, Menu.Main.inventory(player));
    return true;

  }

}
