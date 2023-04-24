package io.wany.amethy.modules.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;

public class RandomTeleport {

  protected static boolean ENABLED = false;
  protected static final String PREFIX = Amethy.COLOR + "§l[랜덤 텔레포트]: §r";

  public static class Preset {

    private double minX = 0;
    private double maxX = 0;
    private double minY = 0;
    private double maxY = 0;
    private double minZ = 0;
    private double maxZ = 0;
    private Collection<Biome> hates = new ArrayList<>();
    private boolean topY = false;

    public Preset(
        double minX, double maxX,
        double minY, double maxY,
        double minZ, double maxZ,
        Collection<Biome> hates) {
      this.minX = minX;
      this.maxX = maxX;
      this.minY = minY;
      this.maxY = maxY;
      this.minZ = minZ;
      this.maxZ = maxZ;
      this.hates = hates;
      this.topY = false;
    }

    public Preset(
        double minX, double maxX,
        double minZ, double maxZ,
        Collection<Biome> hates) {
      this.minX = minX;
      this.maxX = maxX;
      this.minZ = minZ;
      this.maxZ = maxZ;
      this.hates = hates;
      this.topY = true;
    }

    public Preset(
        double minR, double maxR,
        Collection<Biome> hates) {
      this.minX = minR;
      this.maxX = maxR;
      this.minZ = minR;
      this.maxZ = maxR;
      this.hates = hates;
      this.topY = true;
    }

    private double randomIn(double min, double max) {
      double d = max - min;
      double r = Math.floor(Math.random() * d);
      return min + r;
    }

    public void gen(World world, Consumer<Location> consumer) {
      double x = randomIn(this.minX, this.maxX);
      double y = randomIn(this.minY, this.maxY);
      double z = randomIn(this.minZ, this.maxZ);

      int cx = (int) Math.floor(x / 16);
      int cz = (int) Math.floor(z / 16);
      int lx = (int) Math.abs(x % 16);
      int lz = (int) Math.abs(z % 16);

      world.getChunkAtAsync(cx, cz, true, (chunk) -> {
        Location loc;
        if (this.topY) {
          loc = chunk.getBlock(lx, world.getMinHeight(), lz).getLocation().toHighestLocation();
        } else {
          loc = chunk.getBlock(lx, (int) y, lz).getLocation();
        }
        if (hates.contains(loc.getBlock().getBiome())) {
          gen(world, (location) -> consumer.accept(location));
        } else {
          consumer.accept(loc);
        }
      });
    }

  }

  private static HashMap<World, Preset> presets = new HashMap<>();
  private static HashMap<World, List<Location>> locations = new HashMap<>();
  private static HashMap<World, Integer> poolsizes = new HashMap<>();

  private static final ExecutorService onEnableExecutor = Executors.newFixedThreadPool(1);
  private static final Timer onEnableTimer = new Timer();

  public static void onEnable() {
    try {
      Amethy.PLUGIN.registerCommand("randomteleport", new HomeCommand());

      if (!Amethy.YAMLCONFIG.getBoolean("transport.randomteleport.enable")) {
        console.debug(PREFIX + "랜덤 텔레포트 §c비활성화됨");
        return;
      }

      console.debug(PREFIX + "랜덤 텔레포트 §a활성화됨");
      ENABLED = true;

      ConfigurationSection presetsSection = Amethy.YAMLCONFIG
          .getConfigurationSection("transport.randomteleport.presets");
      for (World world : Bukkit.getWorlds()) {
        ConfigurationSection worldPreset = presetsSection.getConfigurationSection(world.getName());
        if (worldPreset != null) {
          List<Biome> hates = new ArrayList<>();
          for (String biomeName : (List<String>) worldPreset.getList("hates")) {
            hates.add(Biome.valueOf(biomeName));
          }
          presets.put(world, new Preset(
              worldPreset.getDouble("radius.min"),
              worldPreset.getDouble("radius.max"),
              hates));
          locations.put(world, new ArrayList<>());
          poolsizes.put(world, worldPreset.getInt("pool"));
        }
      }

      Amethy.PLUGIN.registerCommand("randomteleport", new RandomTeleportCommand());

      onEnableExecutor.submit(() -> {
        onEnableTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            for (World world : Bukkit.getWorlds()) {
              if (locations.get(world) == null) {
                continue;
              }
              if (locations.get(world).size() < poolsizes.get(world)) {
                presets.get(world).gen(world, (location) -> {
                  locations.get(world).add(location);
                  console.debug(PREFIX + "좌표 풀 로드됨 " + world.getName() + ", " + location.getBlockX() + ", "
                      + location.getBlockY() + ", " + location.getBlockZ());
                });
              }
            }
          }
        }, 1000 * 10, 1000 * 10);
      });
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public static void onDisable() {
    if (!ENABLED) {
      return;
    }

    onEnableTimer.cancel();
    onEnableExecutor.shutdown();
  }

  public static void get(World world, Consumer<Location> consumer) {
    if (locations.get(world) == null) {
      new Preset(0, 1000000, new ArrayList<>())
          .gen(world, (location) -> consumer.accept(location));
    }
    if (locations.get(world).size() > 0) {
      consumer.accept(locations.get(world).get(0));
      locations.get(world).remove(0);
    } else {
      presets.get(world).gen(world, (location) -> consumer.accept(location));
    }
  }

}
