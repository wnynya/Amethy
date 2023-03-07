package io.wany.amethy.modules.wand.brush;

import org.bukkit.Location;

import io.wany.amethy.modules.wand.Wand;

public interface BrushToucher {

  void touch(Location location, Wand wand, boolean applyPhysics);

}
