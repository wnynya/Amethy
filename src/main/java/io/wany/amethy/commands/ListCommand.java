package io.wany.amethy.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.wany.amethy.modulesmc.Message;
import io.wany.amethy.supports.vault.VaultSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
    int current = onlinePlayers.size();
    int max = Bukkit.getMaxPlayers();
    HashMap<String, List<Player>> groupPlayers = new HashMap<>();

    if (VaultSupport.SUPPORT.isEnabled() && VaultSupport.CHAT != null && VaultSupport.CHAT.getGroups().length > 0) {
      sender.sendMessage(Component.translatable("commands.list.players").args(Message.parse("§r" + current),
          Message.parse("§r" + max), Message.parse("")));
      for (Player player : onlinePlayers) {
        String group = VaultSupport.CHAT.getPrimaryGroup(player);
        if (!groupPlayers.containsKey(group)) {
          groupPlayers.put(group, new ArrayList<>());
        }
        groupPlayers.get(group).add(player);
      }
      for (String group : groupPlayers.keySet()) {
        List<Player> players = groupPlayers.get(group);
        if (players.size() > 0) {
          List<Component> displayNames = new ArrayList<>();
          for (Player player : players) {
            displayNames.add(Message.formatPlayer(player, "{prefix}§r{displayname}§r{suffix}"));
          }
          String s = "%s, ".repeat(players.size());
          s = s.substring(0, s.length() - 2);
          sender.sendMessage(Message.parse(group, ": ", Component.translatable(s).args(displayNames)));
        }
      }
    } else {

      List<Component> displayNames = new ArrayList<>();
      for (Player player : onlinePlayers) {
        displayNames.add(Message.formatPlayer(player, "{prefix}§r{displayname}§r{suffix}"));
      }
      String s = "%s, ".repeat(onlinePlayers.size());
      if (current > 0) {
        s = s.substring(0, s.length() - 2);
      }

      sender.sendMessage(Component.translatable("commands.list.players").args(Message.parse("§r" + current),
          Message.parse("§r" + max), Component.translatable(s).args(displayNames)));

    }

    return true;

  }

}
