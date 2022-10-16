package io.wany.amethy.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Message;

public class BungeeTeleportCommand implements CommandExecutor {

  private static HashMap<UUID, Long> joinedPlayers = new HashMap<>();
  private static HashMap<UUID, Long> lastUsed = new HashMap<>();

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    Player player = null;
    String server = null;

    if (args.length < 2) {
      sender.sendMessage(Message.of("Usage: " + label + " <player> <server>"));
      return true;
    }

    player = Bukkit.getPlayer(args[0]);
    server = args[1];

    if (player == null) {
      sender.sendMessage(Message.of("플레이어(을)를 찾을(를) 수가는(은) 없습입니다."));
      return true;
    }
    if (server == null) {
      sender.sendMessage(Message.of("서버 가 널 이 다. !"));
      return true;
    }

    UUID uuid = player.getUniqueId();

    if (joinedPlayers.containsKey(uuid)) {
      if (System.currentTimeMillis() - 3000 < joinedPlayers.get(uuid)) {
        return true;
      } else {
        joinedPlayers.remove(uuid);
      }
    }

    if (lastUsed.containsKey(uuid)) {
      if (System.currentTimeMillis() - 2000 < lastUsed.get(uuid)) {
        return true;
      } else {
        lastUsed.replace(uuid, System.currentTimeMillis());
      }
    } else {
      lastUsed.put(uuid, System.currentTimeMillis());
    }

    final String serv = new String(server);
    final Player plyr = player;

    ExecutorService e = Executors.newFixedThreadPool(1);

    e.execute(() -> {
      try {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF("Connect");
        out.writeUTF(serv);
        plyr.sendPluginMessage(Amethy.PLUGIN, "BungeeCord", b.toByteArray());
        b.close();
        out.close();
        e.shutdown();
      } catch (Exception ignored) {
        sender.sendMessage(Message.of(plyr.getName() + "후레이아 을(를) " + serv + " 서 버에연 결하는 데에 성공적이지 못한 실패가 하였습입니다."));
        e.shutdown();
      }
    });

    return true;

  }

  public static void onPlayerJoin(PlayerJoinEvent event) {

    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (joinedPlayers.containsKey(uuid)) {
      joinedPlayers.remove(uuid);
    }

    joinedPlayers.put(uuid, System.currentTimeMillis());

  }
}
