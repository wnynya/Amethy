package io.wany.amethy.modules.sync;

import io.wany.amethyst.Json;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonInventory {

  private final List<ItemStack> contents;

  JsonInventory(Inventory inventory) {
    this.contents = Arrays.asList(inventory.getContents());
  }

  JsonInventory(List<Json> jsonInventory) {
    this.contents = new ArrayList<>();
    for (Json jsonItemStack : jsonInventory) {
      JsonItemStack syncItemStack = new JsonItemStack(jsonItemStack);
      this.contents.add(syncItemStack.getItemStack());
    }
  }

  JsonInventory(List<Json> jsonInventory, Player player) {
    this.contents = new ArrayList<>();
    for (Json jsonItemStack : jsonInventory) {
      JsonItemStack syncItemStack = new JsonItemStack(jsonItemStack, player);
      this.contents.add(syncItemStack.getItemStack());
    }
  }

  protected List<Json> jsonify() {
    List<Json> jsonInventory = new ArrayList<>();
    for (ItemStack itemStack : this.contents) {
      JsonItemStack syncItemStack = new JsonItemStack(itemStack);
      jsonInventory.add(syncItemStack.jsonify());
    }
    return jsonInventory;
  }

  protected void apply(Inventory inventory) {
    inventory.setContents(this.contents.toArray(new ItemStack[this.contents.size()]));
  }

}
