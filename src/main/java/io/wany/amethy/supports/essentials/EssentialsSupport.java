package io.wany.amethy.supports.essentials;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethy.supports.PluginSupport;
import io.wany.amethy.supports.essentials.listeners.UserBalanceUpdate;

public class EssentialsSupport {

  public static PluginSupport SUPPORT;
  public static String PREFIX = "§6§l[Essentials 연동]:§r ";

  public static boolean isEnabled() {
    if (SUPPORT == null) {
      return false;
    } else {
      return SUPPORT.isEnabled();
    }
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("vault-support.enable")) {
      console.debug(PREFIX + "§c비활성화됨");
      return;
    }
    console.debug(PREFIX + "§a활성화됨");

    SUPPORT = new PluginSupport("Essentials");
    SUPPORT.on("enable", (args) -> {
      console.debug(PREFIX + "로드됨");

      Amethy.PLUGIN.registerEvent(new UserBalanceUpdate());
    });
    SUPPORT.ready();
  }

}
