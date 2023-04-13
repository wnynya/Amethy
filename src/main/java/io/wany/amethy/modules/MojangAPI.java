package io.wany.amethy.modules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import io.wany.amethyst.Json;
import io.wany.amethyst.network.HTTPRequest;

public class MojangAPI {

  private static HashMap<String, Object> cache = new HashMap<>();

  public static UUID uuid(String username)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    String key = "MojangAPI_uuid_" + username;
    if (!cache.containsKey(key)) {
      Json res = HTTPRequest.JsonGet("https://api.mojang.com/users/profiles/minecraft/" + username);
      String uuidStr = res.getString("id");
      UUID value = UUID.fromString(uuidStr);
      cache.put(key, value);
    }
    return (UUID) cache.get(key);
  }

  public static Json profile(UUID uuid)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    String key = "MojangAPI_profile_" + uuid.toString();
    if (!cache.containsKey(key)) {
      Json value = HTTPRequest.JsonGet("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
      cache.put(key, value);
    }
    return (Json) cache.get(key);
  }

  public static String username(UUID uuid)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return profile(uuid).getString("name");
  }

}
