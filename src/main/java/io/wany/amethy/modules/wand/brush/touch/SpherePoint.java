package io.wany.amethy.modules.wand.brush.touch;

import org.bukkit.Location;

import io.wany.amethy.modules.wand.Wand;
import io.wany.amethy.modules.wand.area.Area;
import io.wany.amethy.modules.wand.brush.BrushSetting;
import io.wany.amethy.modules.wand.brush.BrushToucher;

import java.util.List;

public class SpherePoint implements BrushToucher {

  public SpherePoint() {
  }

  @Override
  public void touch(Location location, Wand wand, boolean applyPhysics) {
    BrushSetting setting = wand.getBrush().setting;
    List<Location> area = Area.SPHERE_POINTED.getArea(location, setting.getSize());
    wand.storeUndo(area);
    wand.fill(setting.getBlockData(), area, applyPhysics);
  }

}
