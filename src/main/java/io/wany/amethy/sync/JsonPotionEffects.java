package io.wany.amethy.sync;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonPotionEffects {

  public static JsonArray jsonify(List<PotionEffect> potionEffects) {
    JsonArray effects = new JsonArray();
    potionEffects.forEach(potionEffect -> {
      JsonObject effect = new JsonObject();
      NamespacedKey namespacedKey = potionEffect.getType().getKey();
      effect.addProperty("namespace", namespacedKey.getNamespace().toString());
      effect.addProperty("key", namespacedKey.getKey().toString());
      effect.addProperty("amplifier", potionEffect.getAmplifier());
      effect.addProperty("duration", potionEffect.getDuration());
      effects.add(effect);
    });
    return effects;
  }

  public static JsonArray jsonify(Player player) {
    return jsonify(List.copyOf(player.getActivePotionEffects()));
  }

  public static void apply(JsonArray effects, Player player) {
    player.getActivePotionEffects().forEach(effect -> {
      player.removePotionEffect(effect.getType());
    });
    List<PotionEffect> potionEffects = new ArrayList<>();
    effects.forEach(object -> {
      JsonObject effect = object.getAsJsonObject();
      String namespaceString = effect.get("namespace").getAsString();
      String keyString = effect.get("key").getAsString();
      PotionEffectType type = PotionEffectType.getByKey(new NamespacedKey(namespaceString, keyString));
      int amplifier = effect.get("amplifier").getAsInt();
      int duration = effect.get("duration").getAsInt();
      potionEffects.add(new PotionEffect(type, duration, amplifier));
    });
    player.addPotionEffects(potionEffects);
  }

}
