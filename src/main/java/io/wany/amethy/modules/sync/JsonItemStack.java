package io.wany.amethy.modules.sync;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.no_groups.ItemSerializer;

public class JsonItemStack {

  public static String stringify(ItemStack itemStack) {
    itemStack = ItemLore.removeItemLore(itemStack);
    return ItemSerializer.serialize(itemStack);
  }

  public static ItemStack parse(String string) {
    return ItemSerializer.deserialize(string);
  }

  public static ItemStack parse(String string, Player player) {
    return ItemLore.setItemLore(parse(string), player);
  }

}
