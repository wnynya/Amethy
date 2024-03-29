package io.wany.amethy.modules.wand.brush;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import io.wany.amethy.modules.wand.Wand;

public class BrushSetting {

  private final Wand wand;
  private int size = 0;
  private BlockData blockData = Bukkit.createBlockData(Material.AIR);

  public BrushSetting(Wand wand) {
    this.wand = wand;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    int max = 15;
    if (size > max) {
      size = max;
    }
    if (size < 0) {
      size = 0;
    }
    this.size = size;
  }

  public BlockData getBlockData() {
    return blockData;
  }

  public void setBlockData(BlockData data) {
    this.blockData = data;
  }

}
