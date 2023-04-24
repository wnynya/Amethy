package io.wany.amethy.modules.sync;

import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SansPlayer {

  private double health;
  private List<ItemStack> inventory;

  public SansPlayer(double health, List<ItemStack> inventory) {
    this.health = health;
    this.inventory = inventory;
  }

  public double getHealth() {
    return this.health;
  }

  public List<ItemStack> getInventory() {
    return this.inventory;
  }

}
