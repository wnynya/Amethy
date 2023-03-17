package io.wany.amethy.supports.cucumbery;

import io.wany.amethy.Amethy;
import io.wany.amethy.modulesmc.Console;
import io.wany.amethy.supports.PluginSupport;

public class CucumberySupport {

  public static PluginSupport SUPPORT;

  public static boolean isEnabled() {
    if (SUPPORT == null) {
      return false;
    } else {
      return SUPPORT.isEnabled();
    }
  }

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("cucumbery-support.enable")) {
      Console.log("에센셜을 지지하지 않습니다.");
      return;
    }

    SUPPORT = new PluginSupport("Cucumbery");
    SUPPORT.on("enable", (args) -> {
    });
    SUPPORT.ready();
  }

}
