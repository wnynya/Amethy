package io.wany.amethy.modules.sync;

import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.no_groups.ItemSerializer;
import io.wany.amethyst.Json;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JsonItemStack {

  private final ItemStack itemStack;

  protected JsonItemStack(ItemStack itemStack) {
    this.itemStack = itemStack != null ? itemStack.clone() : new ItemStack(Material.AIR);
  }

  protected JsonItemStack(Json jsonItemStack) {
    String itemString = jsonItemStack.getString("citem");
    this.itemStack = ItemSerializer.deserialize(itemString);
  }

  protected JsonItemStack(Json jsonItemStack, Player player) {
    String itemString = jsonItemStack.getString("citem");
    this.itemStack = ItemSerializer.deserialize(itemString);
  }

  protected Json jsonify () {
    ItemStack itemStack = this.itemStack.clone();
    itemStack = ItemLore.removeItemLore(itemStack);
    String itemString = ItemSerializer.serialize(itemStack);
    Json jsonItemStack = new Json();
    jsonItemStack.set("citem", itemString);
    return jsonItemStack;
  }

  protected ItemStack getItemStack () {
    return this.itemStack;
  }

}
