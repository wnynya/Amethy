package io.wany.amethy.supports.essentials;

import io.wany.amethy.Amethy;
import io.wany.amethy.Console;
import io.wany.amethy.supports.PluginSupport;
import io.wany.amethy.supports.essentials.listeners.UserBalanceUpdate;

public class EssentialsSupport {

  public static PluginSupport SUPPORT;

  public static void onEnable() {
    if (!Amethy.YAMLCONFIG.getBoolean("essentials-support.enable")) {
      Console.log("에센셜을 지지하지 않습니다.");
      return;
    }

    SUPPORT = new PluginSupport("Essentials");
    SUPPORT.on("enable", (args) -> {
      Console.debug("에센셩 이벤트가 등록이가 되는것이다");
      Amethy.PLUGIN.registerEvent(new UserBalanceUpdate());
    });
    SUPPORT.ready();
  }

}
