package io.wany.amethy.wand.brush.touch;


import org.bukkit.Location;

import io.wany.amethy.wand.Wand;
import io.wany.amethy.wand.area.Area;
import io.wany.amethy.wand.brush.BrushSetting;
import io.wany.amethy.wand.brush.BrushToucher;

import java.util.List;

public class Cube implements BrushToucher {

  public Cube() {
  }

  @Override
  public void touch(Location location, Wand wand, boolean applyPhysics) {
    BrushSetting setting = wand.getBrush().setting;
    List<Location> area = Area.CUBE.getArea(location, setting.getSize());
    wand.storeUndo(area);
    wand.fill(setting.getBlockData(), area, applyPhysics);
  }

}
