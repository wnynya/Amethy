package io.wany.amethy.modules.sync;

import io.wany.amethyst.Json;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class JsonPotionEffects {

  private final List<PotionEffect> effects;

  public JsonPotionEffects(Player player) {
    effects = List.copyOf(player.getActivePotionEffects());
  }

  public JsonPotionEffects(List<Json> jsonEffects) {
    this.effects = new ArrayList<>();
    for (Json jsonEffect : jsonEffects) {
      String namespace = jsonEffect.getString("namespace");
      String key = jsonEffect.getString("key");
      PotionEffectType type = PotionEffectType.getByKey(new NamespacedKey(namespace, key));
      if (type == null) {
        continue;
      }
      int amplifier = jsonEffect.getInt("amplifier");
      int duration = jsonEffect.getInt("duration");
      this.effects.add(new PotionEffect(type, duration, amplifier));
    }
  }

  protected List<Json> jsonify() {
    List<Json> jsonEffects = new ArrayList<>();
    this.effects.forEach(effect -> {
      Json jsonEffect = new Json();
      NamespacedKey nk = effect.getType().getKey();
      jsonEffect.set("namespace", nk.getNamespace());
      jsonEffect.set("key", nk.getKey());
      jsonEffect.set("amplifier", effect.getAmplifier());
      jsonEffect.set("duration", effect.getDuration());
    });
    return jsonEffects;
  }

  protected void apply(Player player) {
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.addPotionEffects(this.effects);
  }

}
