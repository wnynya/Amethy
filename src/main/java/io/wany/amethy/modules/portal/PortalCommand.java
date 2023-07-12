package io.wany.amethy.modules.portal;

import java.util.Collections;
import java.util.List;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.ServerMessage;
import io.wany.amethy.modules.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import io.wany.amethy.modules.TabExecutor;

public class PortalCommand implements TabExecutor {

  @Override
  public String prefix() {
    return Portal.PREFIX;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    String permPrefix = "amethy.command.";
    int agi = 0;

    if (args.length == agi) {
      // 오류: args[agi] 필요
      error(sender, ServerMessage.ERROR.INSUFFICIENT_ARGS);
      info(sender, "사용법: /" + label + " (version|reload|debug|update|updater|unicode)");
      return true;
    }

    switch (args[agi].toLowerCase()) {

      // 플러그인 버전 확인
      case "create": {
        if (!sender.hasPermission(permPrefix + ".version")) {
          // 오류: 권한 없음
          error(sender, ServerMessage.ERROR.NO_PERM);
          return true;
        }
        String tail;
        try {
          if (Updater.isLatest()) {
            tail = "[최신 버전]";
          }
          else {
            tail = "[업데이트 가능]";
          }
        }
        catch (Exception e) {
          tail = "[버전 확인 실패]";
        }
        // 정보: 플러그인 버전
        info(sender, Amethy.NAME + " v" + Amethy.VERSION + " §o" + tail);
        return true;
      }

      // 플러그인 버전 확인
      case "remove": {
        if (!sender.hasPermission(permPrefix + ".version")) {
          // 오류: 권한 없음
          error(sender, ServerMessage.ERROR.NO_PERM);
          return true;
        }
        String tail;
        try {
          if (Updater.isLatest()) {
            tail = "[최신 버전]";
          }
          else {
            tail = "[업데이트 가능]";
          }
        }
        catch (Exception e) {
          tail = "[버전 확인 실패]";
        }
        // 정보: 플러그인 버전
        info(sender, Amethy.NAME + " v" + Amethy.VERSION + " §o" + tail);
        return true;
      }

      default: {
        // 오류 알 수 없는 args[agi]
        error(sender, ServerMessage.ERROR.UNKNOWN_ARG);
        info(sender, "사용법: /" + label + " (version|reload|debug|update|updater|unicode)");
        return true;
      }

    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return Collections.emptyList();
  }

}
