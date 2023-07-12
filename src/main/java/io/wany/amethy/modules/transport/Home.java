package io.wany.amethy.modules.transport;

import java.util.UUID;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;
import io.wany.amethyst.Json;

public class Home {

  protected static final String prefix = Amethy.COLOR + "§l[홈]: §r";

  private static boolean enabled = false;
  private static Json storage;

  public static boolean isEnabled() {
    return enabled;
  }

  public static HomeData of(UUID uuid) {
    return new HomeData(uuid, storage);
  }

  public static void onEnable() {
    try {
      storage = new Json(Amethy.PLUGIN_DIR.toPath().resolve("transport/home.json").toFile());
      storage.save();

      Amethy.PLUGIN.registerCommand("home", new HomeCommand());
      Amethy.PLUGIN.registerCommand("sethome", new HomeCommand());
      Amethy.PLUGIN.registerCommand("delhome", new HomeCommand());
      Amethy.PLUGIN.registerCommand("visit", new HomeCommand());

      if (!Amethy.YAMLCONFIG.getBoolean("transport.home.enable")) {
        console.debug(prefix + "홈 §c비활성화됨");
        return;
      }

      console.debug(prefix + "홈 §a활성화됨");
      enabled = true;
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
