package io.wany.amethy.supports.cucumbery;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethy.supports.PluginSupport;

public class CucumberySupport {

  public static PluginSupport SUPPORT;
  public static String PREFIX = "§x§5§2§e§e§5§2§l[큐컴버리 연동]:§r";

  public static boolean isEnabled() {
    if (SUPPORT == null) {
      return false;
    }
    else {
      return SUPPORT.isEnabled();
    }
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("cucumbery-support.enable")) {
      console.debug(PREFIX + "§c비활성화됨");
      return;
    }
    console.debug(PREFIX + "§a활성화됨");

    SUPPORT = new PluginSupport("Cucumbery");
    SUPPORT.on("enable", (args) -> {
      console.debug(PREFIX + "로드됨");
    });
    SUPPORT.ready();
  }

}
