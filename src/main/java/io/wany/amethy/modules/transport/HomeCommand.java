package io.wany.amethy.modules.transport;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.wany.amethy.modules.ServerMessage;
import io.wany.amethy.modules.TabExecutor;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

    if (!Home.isEnabled()) {
      error(sender, "홈이 활성화되지 않았습니다.");
      return true;
    }

    if (!(sender instanceof Player player)) {
      error(sender, ServerMessage.ERROR.ONLY_PLAYER);
      return true;
    }

    switch (command.getName().toLowerCase()) {

      case "home" -> {
        HomeData home = Home.of(player.getUniqueId());
        if (home.hasLocation()) {
          player.teleportAsync(home.getLocation());
        }
        else {
          error(sender, "홈이 지정되지 않았습니다. 홈을 지정하려면 /sethome 명령어를 통해 현재 위치를 홈으로 지정하세요.");
        }
        return true;
      }

      case "sethome" -> {
        Location loc = player.getLocation();
        if (!loc.getWorld().getName().equals("world")) {
          error(sender, "홈을 지정할 수 없습니다. 홈은 메인 월드의 오버월드 차원에서만 지정할 수 있습니다.");
          return true;
        }
        HomeData home = Home.of(player.getUniqueId());
        home.setLocation(player.getLocation());
        home.save();
        info(sender, "현재 위치가 홈으로 지정되었습니다. 이제 모든 유저가 이 홈을 방문할 수 있습니다.");
        return true;
      }

      case "delhome" -> {
        HomeData home = Home.of(player.getUniqueId());
        if (home.hasLocation()) {
          home.delete();
          info(sender, "홈이 제거되었습니다.");
        }
        else {
          error(sender, "제거할 홈이 없습니다.");
        }
        return true;
      }

      case "visit" -> {
        if (args.length == 0) {
          error(sender, "대상 플레이어를 입력해주세요.");
        }
        else {
          OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
          HomeData home = Home.of(target.getUniqueId());
          if (home.hasLocation()) {
            player.teleportAsync(home.getLocation());
          }
          else {
            error(sender, "대상 플레이어의 홈이 지정되지 않았습니다.");
          }
        }
        return true;
      }

    }

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

    if (!Home.isEnabled()) {
      return Collections.emptyList();
    }

    if (command.getName().equalsIgnoreCase("visit")) {
      return autoComplete(PreList.PLAYERS(), args[args.length - 1]);
    }

    return Collections.emptyList();
  }

}
