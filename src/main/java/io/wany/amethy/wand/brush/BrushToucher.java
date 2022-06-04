package io.wany.amethy.wand.brush;

import org.bukkit.Location;

import io.wany.amethy.wand.Wand;

public interface BrushToucher {

  void touch(Location location, Wand wand, boolean applyPhysics);

}
