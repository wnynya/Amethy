package io.wany.amethy.wand.brush;

import org.bukkit.Location;

import io.wany.amethy.wand.Wand;
import io.wany.amethy.wand.brush.touch.Cube;
import io.wany.amethy.wand.brush.touch.SpherePoint;
import io.wany.amethy.wand.brush.touch.SphereRound;

public enum Brush implements BrushToucher {

  CUBE {
    @Override
    public void touch(Location location, Wand wand, boolean applyPhysics) {
      new Cube().touch(location, wand, applyPhysics);
    }
  },
  BALL_POINT {
    @Override
    public void touch(Location location, Wand wand, boolean applyPhysics) {
      new SpherePoint().touch(location, wand, applyPhysics);
    }
  },
  BALL_ROUND {
    @Override
    public void touch(Location location, Wand wand, boolean applyPhysics) {
      new SphereRound().touch(location, wand, applyPhysics);
    }
  }

}
