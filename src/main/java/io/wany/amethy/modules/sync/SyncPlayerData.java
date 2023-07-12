package io.wany.amethy.modules.sync;

import io.wany.amethyst.Json;
import org.bukkit.entity.Player;

public class SyncPlayerData {

  private final boolean done;
  private final JsonInventory inventory;
  private final int inventoryPos;
  private final JsonInventory enderChest;
  private final double health;
  private final int level;
  private final float exp;
  private final JsonPotionEffects potionEffects;

  protected SyncPlayerData(Player player, boolean done) {
    this.done = done;

    this.inventory = new JsonInventory(player.getInventory());
    this.inventoryPos = player.getInventory().getHeldItemSlot();
    this.enderChest = new JsonInventory(player.getEnderChest());
    this.health = player.getHealth();
    this.level = player.getLevel();
    this.exp = player.getExp();
    this.potionEffects = new JsonPotionEffects(player);
  }

  protected SyncPlayerData(Json json, Player player) {
    this.done = json.getBoolean("done");

    this.inventory = new JsonInventory(json.getJsonList("inventory"), player);
    this.inventoryPos = json.getInt("inventoryPos");
    this.enderChest = new JsonInventory(json.getJsonList("enderChest"), player);
    this.health = json.getDouble("health");
    this.level = json.getInt("level");
    this.exp = json.getFloat("exp");
    this.potionEffects = new JsonPotionEffects(json.getJsonList("potionEffects"));
  }

  protected Json jsonify() {
    Json data = new Json();
    data.set("done", this.done);

    data.set("inventory", this.inventory.jsonify());
    data.set("inventoryPos", this.inventoryPos);
    data.set("enderChest", this.enderChest.jsonify());
    data.set("health", this.health);
    data.set("level", this.level);
    data.set("exp", this.exp);
    data.set("potionEffects", this.potionEffects.jsonify());
    return data;
  }

  protected void apply(Player player) {
    this.inventory.apply(player.getInventory());
    player.getInventory().setHeldItemSlot(this.inventoryPos);
    this.enderChest.apply(player.getEnderChest());
    player.setHealth(this.health);
    player.setLevel(this.level);
    player.setExp(this.exp);
    this.potionEffects.apply(player);
  }

  protected boolean isDone() {
    return this.done;
  }

}
