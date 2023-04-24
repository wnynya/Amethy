package io.wany.amethy.modules.sync;

import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethyst.Json;

public class DatabaseService {

  private static final DatabaseService instance = new DatabaseService();

  public static DatabaseService get() {
    return instance;
  }

  public SansPlayer getPlayer(UUID uuid) {
    Json data = DatabaseSyncMap.get(uuid.toString() + "...abcd");
    double health = data.getDouble("health");
    List<ItemStack> inventory = JsonInventory.parse(data.getJsonArray("inventory"));
    SansPlayer sp = new SansPlayer(health, inventory);
    return sp;
  }

}
