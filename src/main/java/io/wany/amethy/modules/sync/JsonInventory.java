package io.wany.amethy.modules.sync;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;

public class JsonInventory {

  public static JsonArray jsonify(Inventory inventory) {
    JsonArray contents = new JsonArray();
    for (ItemStack itemStack : inventory.getContents()) {
      String itemString = JsonItemStack.stringify(itemStack);
      contents.add(itemString);
    }
    return contents;
  }

  public static void apply(JsonArray contents, Inventory inventory) {
    List<ItemStack> itemStacks = new ArrayList<>();
    contents.forEach(element -> {
      String itemString = element.getAsString();
      ItemStack itemStack = JsonItemStack.parse(itemString);
      itemStacks.add(itemStack);
    });
    inventory.setContents(itemStacks.toArray(new ItemStack[itemStacks.size()]));
  }

  public static void apply(JsonArray contents, Inventory inventory, Player player) {
    List<ItemStack> itemStacks = new ArrayList<>();
    contents.forEach(element -> {
      String itemString = element.getAsString();
      ItemStack itemStack = JsonItemStack.parse(itemString, player);
      itemStacks.add(itemStack);
    });
    inventory.setContents(itemStacks.toArray(new ItemStack[itemStacks.size()]));
  }

}
