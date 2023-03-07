package io.wany.amethy.modules.wand.area;

import org.bukkit.Location;

import java.util.List;

public interface AreaGenerator {

  List<Location> getArea(Location pos1, Location pos2);

  List<Location> getArea(Location pos, int r);

  List<Location> getArea(Location pos, int r, int h);

}
