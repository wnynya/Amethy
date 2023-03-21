package io.wany.amethy.commands;

import io.wany.amethy.Amethy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.wany.amethy.modulesmc.Message;

public class ExitCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (!(sender instanceof Player player)) {
      return true;
    }

    Amethy.MESSAGE.kick(player, "Bye");

    return true;

  }

}
