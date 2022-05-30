package io.wany.amethy.terminal;

import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
import io.wany.amethy.Config;
import io.wany.amethy.Console;
import io.wany.amethy.modules.Request;

public class Terminal {

  public static String ID;
  private static String KEY = "401790cf28f159d50950333f0856e482";

  public static String getID() {
    Config a = new Config(".a");
    String id = a.getString("id");
    if (id == null) {
      JsonObject res;
      try {
        res = Request.JSONPost(Amethy.API + "/terminal/nodes", new JsonObject(), KEY);
        id = res.get("data").getAsString();
        a.set("id", id);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return id;
  }

  public static void onLoad() {
    ID = getID();
  }

  public static void onEnable() {
  }

  public static void onDisable() {

  }
}
