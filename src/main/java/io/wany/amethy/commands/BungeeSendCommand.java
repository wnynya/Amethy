package io.wany.amethy.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Message;

public class BungeeSendCommand implements CommandExecutor {

  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

    Player target = null;
    String server = null;

    if (sender instanceof Player player) {
      target = player;
    }

    target = Bukkit.getPlayer(args[0]);

    server = args[1];

    if (target == null) {
      sender.sendMessage(Message.of("그런 플레이어 없어"));
      return true;
    }
    if (server == null) {
      sender.sendMessage(Message.of("그런 서버 없어"));
      return true;
    }

    try {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(b);
      out.writeUTF("Connect");
      out.writeUTF(server);
      target.sendPluginMessage(Amethy.PLUGIN, "BungeeCord", b.toByteArray());
      b.close();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
      target.sendMessage(Message.of("Error when trying to connect to " + server));
    }

    return true;

  }

}
