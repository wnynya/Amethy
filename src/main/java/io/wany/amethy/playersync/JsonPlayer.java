package io.wany.amethy.playersync;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import io.wany.amethy.modules.Message;

public class JsonPlayer {

  public static JsonObject temp;

  public static JsonObject jsonify(Player player, boolean isOnline) {
    JsonObject object = new JsonObject();
    object.addProperty("isonline", isOnline);
    object.addProperty("uuid", player.getUniqueId().toString());
    object.addProperty("name", player.getName());
    object.addProperty("displayname", Message.stringify(player.displayName()));
    object.addProperty("health", player.getHealth());
    object.addProperty("healthscale", player.getHealthScale());
    object.addProperty("foodlevel", player.getFoodLevel());
    object.addProperty("exhaution", player.getExhaustion());
    object.addProperty("exp", player.getExp());
    object.addProperty("level", player.getLevel());
    object.add("inventory", JsonInventory.jsonify(player.getInventory()));
    object.add("enderchest", JsonInventory.jsonify(player.getEnderChest()));
    object.add("potioneffects", JsonPotionEffects.jsonify(player));
    object.addProperty("cucumberyeffects", JsonCucumberyEffects.stringiify(player));
    return object;
  }

  public static void apply(JsonObject object, Player player) {
    player.setHealth(object.get("health").getAsDouble());
    player.setHealthScale(object.get("healthscale").getAsDouble());
    player.setFoodLevel(object.get("foodlevel").getAsInt());
    player.setExhaustion(object.get("exhaution").getAsFloat());
    player.setExp(object.get("exp").getAsFloat());
    player.setLevel(object.get("level").getAsInt());
    JsonInventory.apply(object.get("inventory").getAsJsonArray(), player.getInventory(), player);
    JsonInventory.apply(object.get("enderchest").getAsJsonArray(), player.getEnderChest(), player);
    JsonPotionEffects.apply(object.get("potioneffects").getAsJsonArray(), player);
    JsonCucumberyEffects.apply(object.get("cucumberyeffects").getAsString(), player);
  }

  public static void clear(Player player) {
    player.getInventory().setContents(new ItemStack[0]);
    player.getEnderChest().setContents(new ItemStack[0]);
  }

}
