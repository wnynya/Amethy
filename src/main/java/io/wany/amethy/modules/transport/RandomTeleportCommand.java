package io.wany.amethy.modules.transport;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.ServerMessage;
import io.wany.amethy.modules.TabExecutor;

public class RandomTeleportCommand implements TabExecutor {

  @Override
  public String prefix() {
    return Amethy.COLOR + "§l[랜덤 텔레포트]: §r";
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!RandomTeleport.ENABLED) {
      error(sender, "랜덤 텔레포트가 활성화되지 않았습니다.");
      return true;
    }

    if (!(sender instanceof Player player)) {
      error(sender, ServerMessage.ERROR.ONLY_PLAYER);
      return true;
    }

    World world = player.getLocation().getWorld();

    if (args.length > 0) {
      world = Bukkit.getWorld(args[0]);
      if (world == null) {
        error(sender, "월드를 찾을 수 없습니다.");
        return true;
      }
    }

    info(sender, "이동 중입니다. 잠시만 기다려주세요.");
    RandomTeleport.get(world, (location) -> {
      player.teleportAsync(location);
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

    if (!RandomTeleport.ENABLED) {
      return Collections.emptyList();
    }

    return autoComplete(PreList.WORLDS(), args[args.length - 1]);
  }

}
