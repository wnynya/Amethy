package io.wany.amethy.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonObject;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.network.HTTPRequest;
import io.wany.amethy.sync.JsonPlayer;

public class PlayerSync {

  private static boolean enabled = false;
  private static String channel = null;
  private static List<UUID> kicked = new ArrayList<>();

  public static void onPlayerJoin(PlayerJoinEvent event) {
    if (!enabled) {
      return;
    }
    Player player = event.getPlayer();
    JsonObject obj = JsonPlayer.jsonify(player, true);
    JsonPlayer.clear(player);

    Bukkit.getScheduler().runTaskLater(Amethy.PLUGIN, () -> {
      JsonObject object = PlayerSync.get(player.getUniqueId());
      try {
        object.get("isonline").getAsBoolean();
        if (object.get("isonline").getAsBoolean()) {
          kicked.add(player.getUniqueId());
          player.kick(Message.of("데이터 연동 중인 서버에 동시에 접속할 수 없습니다"));
        } else {
          JsonPlayer.apply(object, player);
          PlayerSync.set(player.getUniqueId(), JsonPlayer.jsonify(player, true));
        }
      } catch (Exception e) {
        JsonPlayer.apply(obj, player);
      }
    }, 20L);
  }

  public static void onPlayerQuit(PlayerQuitEvent event) {
    if (!enabled) {
      return;
    }
    Player player = event.getPlayer();
    if (kicked.contains(player.getUniqueId())) {
      kicked.remove(player.getUniqueId());
    } else {
      PlayerSync.set(player.getUniqueId(), JsonPlayer.jsonify(player, false));
    }
  }

  public static JsonObject get(UUID uuid) {
    try {
      return HTTPRequest.JSONGet("https://api.wany.io/amethy/playersync/" + channel + "/" + uuid.toString()).get("data")
          .getAsJsonObject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void set(UUID uuid, JsonObject data) {
    JsonObject body = new JsonObject();
    body.add("data", data);
    try {
      HTTPRequest.JSONPost("https://api.wany.io/amethy/playersync/" + channel + "/" + uuid.toString(), body.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("playersync.enable")) {
      return;
    }
    enabled = true;
    channel = Amethy.CONFIG.getString("playersync.channel");
    channel = channel.replaceAll("[^a-z0-9-_]", "");
  }

}
