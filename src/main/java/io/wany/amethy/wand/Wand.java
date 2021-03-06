package io.wany.amethy.wand;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Campfire;
import org.bukkit.block.Container;
import org.bukkit.block.data.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.Config;
import io.wany.amethy.modules.Console;
import io.wany.amethy.modules.DataTypeChecker;
import io.wany.amethy.modules.Message;
import io.wany.amethy.wand.area.Area;
import io.wany.amethy.wand.command.WandBrushCommand;
import io.wany.amethy.wand.command.WandEditCommand;
import io.wany.amethy.wand.command.WandEditTabCompleter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wand {

  public static String COLORHEX = "#FF782D";
  public static String COLOR = COLORHEX + ";";
  public static String PREFIX = COLOR + "&l[Wand]:&r ";
  public static boolean ENABLED = false;

  public static Config config = null;
  public static int undoLimit = 100;
  private static final HashMap<UUID, Wand> wands = new HashMap<>();

  private static ItemStack wandItem_editPositioner = null;
  private static ItemStack wandItem_brushNormal = null;
  private static ItemStack wandItem_brushApplyPhysics = null;

  public List<WandBlock> clipboard = null;
  private Player player;
  private final UUID uuid;
  private final WandEdit edit;
  private final WandBrush brush;
  private final Stack<List<WandBlock>> undoStack = new Stack<>();
  private final Stack<List<WandBlock>> redoStack = new Stack<>();
  public int lastReplacenearRadius = 5;

  private Wand(UUID uuid) {
    this.player = Bukkit.getPlayer(uuid);
    this.uuid = uuid;
    this.edit = new WandEdit(this);
    this.brush = new WandBrush(this);
    wands.put(uuid, this);
    this.edit.startParticleArea();
  }

  private Wand(Player player) {
    this.player = player;
    this.uuid = player.getUniqueId();
    this.edit = new WandEdit(this);
    this.brush = new WandBrush(this);
    wands.put(uuid, this);
    this.edit.startParticleArea();
  }

  public WandEdit getEdit() {
    return this.edit;
  }

  public WandBrush getBrush() {
    return this.brush;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void refreshPlayer(PlayerJoinEvent event) {
    this.player = event.getPlayer();
  }

  public boolean undo(boolean applyPhysics) {
    if (this.undoStack.size() == 0) {
      return false;
    }
    List<WandBlock> wandBlocks = undoStack.peek();
    List<Location> area = new ArrayList<>();
    for (WandBlock wandBlock : wandBlocks) {
      area.add(wandBlock.getLocation());
    }
    storeRedo(area);
    undoStack.pop();
    for (WandBlock wandBlock : wandBlocks) {
      Block block = wandBlock.getLocation().getBlock();
      wandBlock.apply(block, applyPhysics);
    }
    return true;
  }

  /**
   * ????????? ??????????????? ???????????????.
   *
   * @param applyPhysics ?????? ?????? ??????
   */
  public boolean redo(boolean applyPhysics) {
    if (this.redoStack.size() == 0) {
      return false;
    }
    List<WandBlock> wandBlocks = redoStack.peek();
    List<Location> area = new ArrayList<>();
    for (WandBlock wandBlock : wandBlocks) {
      area.add(wandBlock.getLocation());
    }
    storeUndo(area);
    redoStack.pop();
    for (WandBlock wandBlock : wandBlocks) {
      Block block = wandBlock.getLocation().getBlock();
      wandBlock.apply(block, applyPhysics);
    }
    return true;
  }

  /**
   * ???????????? ???????????? ????????? ??? ????????? ???????????????.
   *
   * @param area ?????????
   */
  public void storeUndo(List<Location> area) {
    List<WandBlock> wandBlocks = new ArrayList<>();
    for (Location loc : area) {
      Block block = loc.getBlock();
      WandBlock wandBlock = new WandBlock(block);
      wandBlocks.add(wandBlock);
    }
    if (undoStack.size() >= undoLimit) {
      undoStack.remove(0);
    }
    undoStack.push(wandBlocks);
  }

  /**
   * ???????????? ??????????????? ????????? ??? ????????? ???????????????.
   *
   * @param area ?????????
   */
  public void storeRedo(List<Location> area) {
    List<WandBlock> wandBlocks = new ArrayList<>();
    for (Location loc : area) {
      Block block = loc.getBlock();
      WandBlock wandBlock = new WandBlock(block);
      wandBlocks.add(wandBlock);
    }
    if (redoStack.size() >= undoLimit) {
      redoStack.remove(0);
    }
    redoStack.push(wandBlocks);
  }

  /**
   * ???????????? ???????????? ?????? ?????? ???????????? ??????????????? ???????????????.
   *
   * @param area      ????????? ?????? ??????
   * @param pos       ?????? ??????
   * @param blackList ???????????? ?????? ??????
   */
  public void copy(List<Location> area, Location pos, List<Material> blackList) {
    List<WandBlock> wandBlocks = new ArrayList<>();
    for (Location loc : area) {
      Block block = loc.getBlock();
      if (blackList != null && blackList.contains(block.getType())) {
        continue;
      }
      WandBlock wandBlock = new WandBlock(block);
      wandBlock.setLocation(new Location(loc.getWorld(), (int) loc.getX() - pos.getX(), (int) loc.getY() - pos.getY(),
          (int) loc.getZ() - pos.getZ()));
      wandBlocks.add(wandBlock);
    }
    this.clipboard = wandBlocks;
  }

  /**
   * ??????????????? ?????? ???????????? ?????? ?????? ???????????? ???????????????.
   *
   * @param pos          ?????? ??????
   * @param blackList    ???????????? ?????? ??????
   * @param applyPhysics ?????? ?????? ??????
   */
  public void paste(Location pos, List<Material> blackList, boolean applyPhysics) {
    if (pos == null) {
      return;
    }

    for (WandBlock wandBlock : this.clipboard) {
      if (blackList != null && blackList.contains(wandBlock.getBlockData().getMaterial())) {
        continue;
      }
      Location location = new Location(
          pos.getWorld(),
          (int) wandBlock.getLocation().getX() + pos.getX(),
          (int) wandBlock.getLocation().getY() + pos.getY(),
          (int) wandBlock.getLocation().getZ() + pos.getZ());
      Block block = location.getBlock();
      wandBlock.apply(block, applyPhysics);
    }
  }

  /**
   * [WIP]
   * ??????????????? ?????? ???????????? ????????? ???????????? ????????????.
   *
   * @param x x ????????? ????????? ??????
   * @param y y ????????? ????????? ??????
   * @param z z ????????? ????????? ??????
   */
  public void multiply(int x, int y, int z) {
    if (this.clipboard == null) {
      return;
    }

    for (WandBlock wandBlock : this.clipboard) {
      Location loc = new Location(wandBlock.getLocation().getWorld(), (int) wandBlock.getLocation().getZ() * x,
          (int) wandBlock.getLocation().getY() * y, (int) wandBlock.getLocation().getX() * z);
      wandBlock.setLocation(loc);
    }

  }

  /**
   * [WIP]
   * ??????????????? ?????? ???????????? ?????? ???????????? ??????????????????.
   *
   * @param facing ???????????? ??????
   */
  public void rotate(String facing) {
    if (this.clipboard == null) {
      return;
    }

    List<BlockFace> blockFaces = List.of(
        BlockFace.EAST_NORTH_EAST,
        BlockFace.EAST,
        BlockFace.EAST_SOUTH_EAST,
        BlockFace.SOUTH_EAST,
        BlockFace.SOUTH_SOUTH_EAST,
        BlockFace.SOUTH,
        BlockFace.SOUTH_SOUTH_WEST,
        BlockFace.SOUTH_WEST,
        BlockFace.WEST_SOUTH_WEST,
        BlockFace.WEST,
        BlockFace.WEST_NORTH_WEST,
        BlockFace.NORTH_WEST,
        BlockFace.NORTH_NORTH_WEST,
        BlockFace.NORTH,
        BlockFace.NORTH_NORTH_EAST,
        BlockFace.NORTH_EAST);

    switch (facing) {
      case "right" -> {
        multiply(-1, 1, 1);
        for (WandBlock wandBlock : this.clipboard) {
          BlockData blockData = wandBlock.getBlockData();
          Console.log(blockData.toString());
          if (blockData instanceof Directional directional) {
            int facingIndex = blockFaces.indexOf(directional.getFacing());
            facingIndex += 4;
            facingIndex = facingIndex % 16;
            directional.setFacing(blockFaces.get(facingIndex));
          }
          if (blockData instanceof Rotatable rotatable) {
            int facingIndex = blockFaces.indexOf(rotatable.getRotation());
            facingIndex += 4;
            facingIndex = facingIndex % 16;
            rotatable.setRotation(blockFaces.get(facingIndex));
          }
          if (blockData instanceof MultipleFacing multipleFacing) {
            boolean east = multipleFacing.hasFace(BlockFace.EAST);
            boolean south = multipleFacing.hasFace(BlockFace.SOUTH);
            boolean west = multipleFacing.hasFace(BlockFace.WEST);
            boolean north = multipleFacing.hasFace(BlockFace.NORTH);
            multipleFacing.setFace(BlockFace.EAST, north);
            multipleFacing.setFace(BlockFace.SOUTH, east);
            multipleFacing.setFace(BlockFace.WEST, west);
            multipleFacing.setFace(BlockFace.NORTH, south);
          }
          if (blockData instanceof Rail rail) {
            Rail.Shape shape = rail.getShape();
            switch (shape) {
              case ASCENDING_EAST -> rail.setShape(Rail.Shape.ASCENDING_SOUTH);
              case ASCENDING_SOUTH -> rail.setShape(Rail.Shape.ASCENDING_WEST);
              case ASCENDING_WEST -> rail.setShape(Rail.Shape.ASCENDING_NORTH);
              case ASCENDING_NORTH -> rail.setShape(Rail.Shape.ASCENDING_EAST);
              case SOUTH_EAST -> rail.setShape(Rail.Shape.SOUTH_WEST);
              case SOUTH_WEST -> rail.setShape(Rail.Shape.NORTH_WEST);
              case NORTH_WEST -> rail.setShape(Rail.Shape.NORTH_EAST);
              case NORTH_EAST -> rail.setShape(Rail.Shape.SOUTH_EAST);
              case EAST_WEST -> rail.setShape(Rail.Shape.NORTH_SOUTH);
              case NORTH_SOUTH -> rail.setShape(Rail.Shape.EAST_WEST);
            }
          }
          wandBlock.setBlockData(blockData);
        }
      }
      case "left" -> multiply(1, 1, -1);
      case "up" -> {
        for (WandBlock wandBlock : this.clipboard) {
          Location loc = new Location(wandBlock.getLocation().getWorld(), (int) wandBlock.getLocation().getZ(),
              (int) wandBlock.getLocation().getY() * -1, (int) wandBlock.getLocation().getX());
          wandBlock.setLocation(loc);
        }
      }
    }

  }

  /**
   * [WIP]
   * ??????????????? ?????? ???????????? ?????? ???????????? ??????????????????.
   *
   * @param facing ???????????? ??????
   */
  public void flip(String facing) {
    if (this.clipboard == null) {
      return;
    }

    switch (facing) {
      case "right" -> {
        for (WandBlock wandBlock : this.clipboard) {
          Location loc = new Location(wandBlock.getLocation().getWorld(), (int) wandBlock.getLocation().getZ(),
              (int) wandBlock.getLocation().getY(), (int) wandBlock.getLocation().getX() * -1);
          wandBlock.setLocation(loc);
        }
      }
      case "left" -> {
        for (WandBlock wandBlock : this.clipboard) {
          Location loc = new Location(wandBlock.getLocation().getWorld(), (int) wandBlock.getLocation().getZ(),
              (int) wandBlock.getLocation().getY(), (int) wandBlock.getLocation().getX());
          wandBlock.setLocation(loc);
        }
      }
      case "up" -> {
        for (WandBlock wandBlock : this.clipboard) {
          Location loc = new Location(wandBlock.getLocation().getWorld(), (int) wandBlock.getLocation().getZ(),
              (int) wandBlock.getLocation().getY() * -1, (int) wandBlock.getLocation().getX());
          wandBlock.setLocation(loc);
        }
      }
    }

  }

  /**
   * ???????????? ?????? ???????????? ????????????.
   *
   * @param blockData    ?????? ??????
   * @param area         ?????????
   * @param applyPhysics ?????? ?????? ??????
   */
  public void fill(BlockData blockData, List<Location> area, boolean applyPhysics) {
    World world = area.get(0).getWorld();

    if (world == null) {
      return;
    }

    for (Location pos : area) {
      Block block = world.getBlockAt(pos);
      boolean need2removeBlockData = false;

      if (block.getState() instanceof Container container) {
        container.getInventory().setContents(new ItemStack[0]);
        container.update(true, false);
        need2removeBlockData = true;
      }

      if (block.getState() instanceof BlockInventoryHolder blockInventoryHolder) {
        Inventory i = blockInventoryHolder.getInventory();
        i.clear();
        need2removeBlockData = true;
      }

      if (block.getState() instanceof Campfire campfire) {
        for (int n = 0; n < 4; n++) {
          campfire.setItem(n, new ItemStack(Material.AIR));
        }
        campfire.update(true, false);
        need2removeBlockData = true;
      }

      if (block.getBlockData() instanceof Waterlogged waterlogged) {
        waterlogged.isWaterlogged();
        need2removeBlockData = true;
      }

      if (need2removeBlockData) {
        block.setBlockData(Bukkit.createBlockData(Material.AIR), false);
      }

      block.setBlockData(blockData, applyPhysics);
    }
  }

  /**
   * ???????????? ???????????? ???????????????.
   *
   * @param area         ?????????
   * @param applyPhysics ?????? ?????? ??????
   */
  public void remove(List<Location> area, boolean applyPhysics) {
    BlockData blockData = Bukkit.createBlockData(Material.AIR, "[]");
    fill(blockData, area, applyPhysics);
  }

  /**
   * ???????????? ?????? ????????? ?????? ???????????? ????????????.
   *
   * @param originalBlockData ?????? ??????
   * @param replaceBlockData  ?????? ??????
   * @param area              ?????????
   * @param applyPhysics      ?????? ?????? ??????
   */
  public void replace(BlockData originalBlockData, BlockData replaceBlockData, List<Location> area,
      boolean applyPhysics) {
    World world = area.get(0).getWorld();

    if (world == null) {
      return;
    }

    for (Location pos : area) {
      Block block = world.getBlockAt(pos);
      if (originalBlockData.getAsString().equals("[]") || originalBlockData.getAsString().equals("")) {
        if (!block.getType().equals(originalBlockData.getMaterial())) {
          continue;
        }
      } else {
        if (!block.getBlockData().equals(originalBlockData)) {
          continue;
        }
      }

      if (block.getState() instanceof Container container) {
        container.getInventory().setContents(new ItemStack[0]);
      }

      block.setBlockData(replaceBlockData, applyPhysics);
    }
  }

  /**
   * ???????????? ?????? ????????? ????????????.
   *
   * @param area      ?????????
   * @param blockData ?????? ??????
   * @param matchData ?????? ????????? ?????? ??????
   * @return Location ???????????? ???????????????.
   */
  public List<Location> scan(List<Location> area, BlockData blockData, boolean matchData) {
    World world = area.get(0).getWorld();

    if (world == null) {
      return null;
    }

    List<Location> scannedBlocks = new ArrayList<>();

    for (Location pos : area) {
      Block block = world.getBlockAt(pos);

      if (matchData) {
        if (block.getBlockData().equals(blockData)) {
          scannedBlocks.add(pos);
        }
      } else {
        if (block.getType().equals(blockData.getMaterial())) {
          scannedBlocks.add(pos);
        }
      }
    }

    return scannedBlocks;
  }

  /**
   * ???????????? ???????????? ?????? ???????????? n ??? ???????????????.
   *
   * @param pos1         ????????? ?????? ??????
   * @param pos2         ????????? ??? ??????
   * @param dir          ????????? ??????
   * @param n            ????????? ??????
   * @param applyPhysics ?????? ?????? ??????
   */
  public void stack(Location pos1, Location pos2, String dir, int n, boolean applyPhysics) {

    if (pos1 == null || pos2 == null || dir == null || n <= 0) {
      return;
    }

    if (n > 1000) {
      return;
    }

    int minX = (int) Math.min(pos1.getX(), pos2.getX());
    int minY = (int) Math.min(pos1.getY(), pos2.getY());
    int minZ = (int) Math.min(pos1.getZ(), pos2.getZ());
    int maxX = (int) Math.max(pos1.getX(), pos2.getX());
    int maxY = (int) Math.max(pos1.getY(), pos2.getY());
    int maxZ = (int) Math.max(pos1.getZ(), pos2.getZ());

    List<WandBlock> area = new ArrayList<>();
    List<Location> locArea = Area.CUBE.getArea(pos1, pos2);
    for (Location loc : locArea) {
      Block block = loc.getBlock();
      WandBlock wandBlock = new WandBlock(block);
      wandBlock.setLocation(new Location(loc.getWorld(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ()));
      area.add(wandBlock);
    }
    World world = pos1.getWorld();

    if (world == null) {
      return;
    }

    for (int m = 1; m <= n; m++) {
      for (WandBlock wandBlock : area) {
        Location loc = wandBlock.getLocation();
        Location newLoc;
        switch (dir) {
          case "east":
            newLoc = new Location(world, loc.getX() + (m * ((maxX + 1) - minX)), loc.getY(), loc.getZ());
            break;
          case "west":
            newLoc = new Location(world, loc.getX() - (m * ((maxX + 1) - minX)), loc.getY(), loc.getZ());
            break;
          case "south":
            newLoc = new Location(world, loc.getX(), loc.getY(), loc.getZ() + (m * ((maxZ + 1) - minZ)));
            break;
          case "north":
            newLoc = new Location(world, loc.getX(), loc.getY(), loc.getZ() - (m * ((maxZ + 1) - minZ)));
            break;
          case "up":
            newLoc = new Location(world, loc.getX(), loc.getY() + (m * ((maxY + 1) - minY)), loc.getZ());
            break;
          case "down":
            newLoc = new Location(world, loc.getX(), loc.getY() - (m * ((maxY + 1) - minY)), loc.getZ());
            break;
          default:
            return;
        }
        Block block = world.getBlockAt(newLoc);

        if (block.getState() instanceof Container container) {
          container.getInventory().setContents(new ItemStack[0]);
        }

        wandBlock.apply(block, applyPhysics);
      }
    }

  }

  /**
   * Get Wand
   */
  public static Wand getWand(UUID uuid) {
    if (wands.containsKey(uuid)) {
      return wands.get(uuid);
    } else {
      return new Wand(uuid);
    }
  }

  public static Wand getWand(Player player) {
    if (wands.containsKey(player.getUniqueId())) {
      return wands.get(player.getUniqueId());
    } else {
      return new Wand(player);
    }
  }

  public static void onPlayerJoin(PlayerJoinEvent event) {

    if (!Wand.ENABLED) {
      return;
    }

    getWand(event.getPlayer()).refreshPlayer(event);

    WandEdit.onPlayerJoin(event);

  }

  public static void onPlayerQuit(PlayerQuitEvent event) {

    if (!Wand.ENABLED) {
      return;
    }

    WandEdit.onPlayerQuit(event);

  }

  /*
   * public void replaceData(int x, int y, int z) {
   * return;
   * if (this.clipboardMemory == null) {
   * return;
   * }
   * 
   * for (WandBlock wandBlock : this.clipboardMemory) {
   * BlockData blockData = wandBlock.getBlockData();
   * if (!(blockData instanceof Directional)) {
   * continue;
   * }
   * Directional directional = (Directional) blockData;
   * BlockFace face = directional.getFacing();
   * BlockFace newFace;
   * switch (face) {
   * case NORTH: {
   * face = BlockFace.EAST;
   * break;
   * }
   * case EAST: {
   * face = BlockFace.SOUTH;
   * break;
   * }
   * case SOUTH: {
   * face = BlockFace.WEST;
   * break;
   * }
   * case WEST: {
   * face = BlockFace.NORTH;
   * break;
   * }
   * directional.setFacing(face);
   * }
   * wandBlock.setLocation(loc);
   * }
   * }
   */

  public enum ItemType {

    EDIT_POSITIONER, BRUSH_NORMAL, BRUSH_APPLYPHYSICS

  }

  public static void info(CommandSender sender, String message) {
    Message.info(sender, Message.effect(PREFIX + "&r" + message));
  }

  public static void warn(CommandSender sender, String message) {
    Message.info(sender, Message.effect(PREFIX + "&r&e" + message));
  }

  public static void error(CommandSender sender, String message) {
    Message.info(sender, Message.effect(PREFIX + "&r&c" + message));
  }

  public static String blockDataBeauty(BlockData blockData) {
    String msg = "";
    String s = blockData.getAsString();
    Pattern p = Pattern.compile("(.*):(.*)\\[(.*)\\]");
    Matcher m = p.matcher(s);

    if (m.find()) {
      if (m.group(1).equals("minecraft")) {
        msg += "&r&e" + m.group(2) + " &7[";
      } else {
        msg += "&r&8" + m.group(1) + ":&e" + m.group(2) + " &7[";
      }
      String data = m.group(3);
      String[] ds = data.split(",");
      Pattern p2 = Pattern.compile("(.*)=(.*)");
      for (String d : ds) {
        Matcher m2 = p2.matcher(d);
        if (m2.find()) {
          msg += "&3" + m2.group(1) + "&7=";
          String v = m2.group(2);
          if (v.equals("true")) {
            msg += "&a" + v;
          } else if (v.equals("false")) {
            msg += "&c" + v;
          } else if (DataTypeChecker.isInteger(v)) {
            msg += "&6" + v;
          } else {
            msg += "&b" + v;
          }
          msg += "&7, ";
        }
      }
      msg = msg.substring(0, msg.length() - 4);
      msg += "&7]";
    }

    return msg;
  }

  public static boolean exist(UUID uuid) {
    return wands.containsKey(uuid);
  }

  public static BlockData getBlockData(String string) throws Exception {

    Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)(\\[(.*)\\])?");

    Matcher matcher = pattern.matcher(string);
    if (matcher.find()) {
      String name = matcher.group(1);
      String data = matcher.group(3);

      if (data == null) {
        data = "";
      }

      Material material;
      try {
        material = Material.valueOf(name.toUpperCase());
      } catch (Exception e) {
        throw e;
      }
      if (!material.isBlock()) {
        throw new Exception("not block");
      }

      BlockData blockData;
      try {
        blockData = Bukkit.createBlockData(material, "[" + data + "]");
      } catch (Exception e) {
        throw e;
      }
      return blockData;
    }

    throw new Exception("string parsing error");

  }

  public static String getDataString(String string) {

    Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)(\\[(.*)\\])?");

    Matcher matcher = pattern.matcher(string);
    if (matcher.find()) {
      if (matcher.group(2) != null) {
        return matcher.group(2);
      }
    }

    return "";

  }

  public static ItemStack getWandItem(ItemType itemType) {
    switch (itemType) {
      case EDIT_POSITIONER: {
        ItemStack item = wandItem_editPositioner;
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(Message.effect("&6&l?????? ??????"));
        List<String> lore = new ArrayList<String>();
        lore.add(Message.effect(""));
        lore.add(Message.effect("&7????????? &9&o????????? / ?????? ??????&7?????? &a???????????? ??????&7??? ??? ????????????."));
        lore.add(Message.effect(""));
        lore.add(Message.effect("&7????????? &9&o????????? / ?????? ??????&7?????? &c???????????? ?????? ??????&7??? ??? ????????????."));
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
      }

      case BRUSH_NORMAL: {
        return wandItem_brushNormal;
      }

      case BRUSH_APPLYPHYSICS: {
        return wandItem_brushApplyPhysics;
      }

      default:
        return null;
    }
  }

  // ??????
  public static void load() {

    Material material;
    Material mat = Material.getMaterial(Amethy.CONFIG.getString("wand.edit.normal-item"));
    if (mat != null) {
      material = mat;
    } else {
      material = Material.SWEET_BERRIES;
    }
    wandItem_editPositioner = new ItemStack(material);

    material = Material.SPECTRAL_ARROW;
    wandItem_brushNormal = new ItemStack(material);

    material = Material.TIPPED_ARROW;
    wandItem_brushApplyPhysics = new ItemStack(material);

    if (Amethy.CONFIG.isInt("wand.undo-limit")) {
      undoLimit = Amethy.CONFIG.getInt("wand.undo-limit");
      Console.debug(PREFIX + "Set undo-limit to " + undoLimit);
    }

  }

  public static void onEnable() {

    if (!Amethy.CONFIG.getBoolean("wand.enable")) {
      Console.debug(Message.effect(PREFIX + "Wand Disabled"));
      return;
    }
    Console.debug(Message.effect(PREFIX + "Enabling Wand"));
    Wand.ENABLED = true;

    Amethy.PLUGIN.registerCommand("wandedit", new WandEditCommand(), new WandEditTabCompleter());
    Amethy.PLUGIN.registerCommand("wandbrush", new WandBrushCommand(), new WandEditTabCompleter());

    load();

  }

  public static void onDisable() {
    for (Wand wand : wands.values()) {
      WandEdit edit = wand.getEdit();
      edit.stopParticleArea();
    }
  }

}
