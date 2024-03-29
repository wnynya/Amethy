package io.wany.amethy.modules.wand.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import io.wany.amethy.modulesmc.DataTypeChecker;
import io.wany.amethy.modulesmc.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WandEditTabCompleter implements org.bukkit.command.TabCompleter {

  public static List<String> autoComplete(List<String> list, String arg) {
    if (!arg.equalsIgnoreCase("")) {
      List<String> listA = new ArrayList<>();
      for (String value : list) {
        if (value.toLowerCase().contains(arg.toLowerCase())) {
          listA.add(value);
        }
      }
      return sort(listA);
    }
    return sort(list);
  }

  private static List<String> sort(List<String> list) {
    Collections.sort(list);
    return list;
  }

  public static void usedFlags(String[] args, int commandArgsLength, List<String> list) {
    int n = 0;
    for (String arg : args) {
      if (n >= commandArgsLength) {
        if (arg.equals("-silent") || arg.equals("-s")) {
          list.remove("-silent");
          list.remove("-s");
        }
        if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
          list.remove("-applyPhysics");
          list.remove("-ap");
        }
      }
      n++;
    }
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
      String[] args) {

    if (args.length <= 1) {
      List<String> list = new ArrayList<>();
      if (sender.hasPermission("cherry.wand.edit.get")) {
        list.add("get");
      }
      if (sender.hasPermission("cherry.wand.undo")) {
        list.add("undo");
      }
      if (sender.hasPermission("cherry.wand.redo")) {
        list.add("redo");
      }
      if (sender.hasPermission("cherry.wand.edit.stack")) {
        list.add("stack");
      }
      if (sender.hasPermission("cherry.wand.edit.pos")) {
        list.addAll(List.of("pos1", "pos2"));
      }
      if (sender.hasPermission("cherry.wand.edit.copy")) {
        list.add("copy");
      }
      if (sender.hasPermission("cherry.wand.edit.cut")) {
        list.add("cut");
      }
      if (sender.hasPermission("cherry.wand.edit.paste")) {
        list.add("paste");
      }
      if (sender.hasPermission("cherry.wand.edit.rotate")) {
        list.add("rotate");
      }
      if (sender.hasPermission("cherry.wand.edit.replace")) {
        list.add("replace");
      }
      if (sender.hasPermission("cherry.wand.edit.replacenear")) {
        list.add("replacenear");
      }
      if (sender.hasPermission("cherry.wand.edit.cube")) {
        list.addAll(List.of("cube", "emptycube", "walledcube"));
        list.addAll(List.of("ecube", "wcube"));
      }
      if (sender.hasPermission("cherry.wand.edit.cyl")) {
        list.addAll(List.of("cyl", "emptycyl"));
        list.addAll(List.of("pointcyl", "emptypointcyl"));
        list.addAll(List.of("ecyl", "pcyl", "epcyl"));
      }
      if (sender.hasPermission("cherry.wand.edit.sphere")) {
        list.addAll(List.of("sphere", "emptysphere"));
        list.addAll(List.of("pointsphere", "emptypointsphere"));
        list.addAll(List.of("esphere", "psphere", "epsphere"));
      }
      if (sender.hasPermission("cherry.wand.edit.wall")) {
        list.add("wall");
      }

      return autoComplete(list, args[args.length - 1]);
    }

    String arg0 = args[0].toLowerCase();

    switch (arg0) {

      case "undo", "redo" -> {
        if ((!sender.hasPermission("cherry.wand.undo") && args[0].equals("undo"))
            || (!sender.hasPermission("cherry.wand.redo") && args[0].equals("redo"))) {
          return Collections.emptyList();
        }

        int commandArgsLength = 3;

        if (args.length == 2) {
          return onIntegerTabComplete("반복 횟수", 1000, 1, args[args.length - 1]);
        }

        if (args.length == 3) {
          List<String> list = new ArrayList<>(Tool.Lista.playerNames());
          return autoComplete(list, args[args.length - 1]);
        }

        // flags
        List<String> flags = List.of("-silent", "-applyPhysics");
        if (args.length <= commandArgsLength + flags.size()) {
          List<String> list = new ArrayList<>(flags);
          usedFlags(args, commandArgsLength, list);
          return autoComplete(list, args[args.length - 1]);
        }

        return Collections.emptyList();
      }

      case "pos1", "pos2" -> {
        if (!sender.hasPermission("cherry.wand.edit.pos")) {
          return Collections.emptyList();
        }

        int commandArgsLength = 1;

        if (sender.hasPermission("cherry.wand.edit.pos.location")) {
          commandArgsLength = 5;
          // location x y z world
          if (args.length == 2) {
            List<String> list = new ArrayList<>(List.of("x"));
            if (sender instanceof Player player) {
              Block block = player.getTargetBlock(10);
              if (block != null) {
                if (!args[args.length - 1].equals("")) {
                  list = new ArrayList<>(List.of(
                      args[args.length - 1] + "",
                      args[args.length - 1] + " " + block.getY(),
                      args[args.length - 1] + " " + block.getY() + " " + block.getZ(),
                      args[args.length - 1] + " " + block.getY() + " " + block.getZ() + " "
                          + block.getWorld().getName()));
                } else {
                  list = new ArrayList<>(List.of(
                      block.getX() + "",
                      block.getX() + " " + block.getY(),
                      block.getX() + " " + block.getY() + " " + block.getZ(),
                      block.getX() + " " + block.getY() + " " + block.getZ() + " " + block.getWorld().getName()));
                }
              }
            }
            return autoComplete(list, args[args.length - 1]);
          }
          if (args.length == 3) {
            List<String> list = new ArrayList<>(List.of("y"));
            if (sender instanceof Player player) {
              Block block = player.getTargetBlock(10);
              if (block != null) {
                if (!args[args.length - 1].equals("")) {
                  list = new ArrayList<>(List.of(
                      args[args.length - 1] + "",
                      args[args.length - 1] + " " + block.getZ(),
                      args[args.length - 1] + " " + block.getZ() + " " + block.getWorld().getName()));
                } else {
                  list = new ArrayList<>(List.of(
                      block.getY() + "",
                      block.getY() + " " + block.getZ(),
                      block.getY() + " " + block.getZ() + " " + block.getWorld().getName()));
                }
              }
            }
            return autoComplete(list, args[args.length - 1]);
          }
          if (args.length == 4) {
            List<String> list = new ArrayList<>(List.of("z"));
            if (sender instanceof Player player) {
              Block block = player.getTargetBlock(10);
              if (block != null) {
                if (!args[args.length - 1].equals("")) {
                  list = new ArrayList<>(List.of(
                      args[args.length - 1] + "",
                      args[args.length - 1] + " " + block.getWorld().getName()));
                } else {
                  list = new ArrayList<>(List.of(
                      block.getZ() + "",
                      block.getZ() + " " + block.getWorld().getName()));
                }
              }
            }
            return autoComplete(list, args[args.length - 1]);
          }
          if (args.length == 5) {
            List<String> list = new ArrayList<>();
            for (World world : Bukkit.getWorlds()) {
              list.add(world.getName());
            }
            return autoComplete(list, args[args.length - 1]);
          }
        }

        // flags
        List<String> flags = List.of("-silent");
        if (args.length <= commandArgsLength + flags.size()) {
          List<String> list = new ArrayList<>(flags);
          usedFlags(args, commandArgsLength, list);
          return autoComplete(list, args[args.length - 1]);
        }

        return Collections.emptyList();
      }

    }

    Player sendPlayer = null;
    if (sender instanceof Player) {
      sendPlayer = (Player) sender;
    }

    if (command.getName().equalsIgnoreCase("wandedit")) {

      args[0] = args[0].toLowerCase();

      if (args[0].equals("copy")) {
        if (!sender.hasPermission("cherry.wand.copy")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockX() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockX() + "");
              }
            }
            return Collections.singletonList("<X>");
          } else {
            return onIntegerTabComplete("X", 30000000, -30000000, args[args.length - 1]);
          }
        }

        if (args.length == 3) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockY() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockY() + "");
              }
            }
            return Collections.singletonList("<Y>");
          } else {
            return onIntegerTabComplete("X", 256, 0, args[args.length - 1]);
          }
        }

        if (args.length == 4) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockZ() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockZ() + "");
              }
            }
            return Collections.singletonList("<Z>");
          } else {
            return onIntegerTabComplete("Z", 30000000, -30000000, args[args.length - 1]);
          }
        }

        if (args.length == 5) {
          List<String> list = new ArrayList<>();
          return autoComplete(Tool.Lista.worldNames(), args[args.length - 1]);
        }

        int commandArgsLength = 5;
        if (args.length <= commandArgsLength + 1) {
          List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("paste")) {
        if (!sender.hasPermission("cherry.wand.paste")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockX() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockX() + "");
              }
            }
            return Collections.singletonList("<X>");
          } else {
            return onIntegerTabComplete("X", 30000000, -30000000, args[args.length - 1]);
          }
        }

        if (args.length == 3) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockY() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockY() + "");
              }
            }
            return Collections.singletonList("<Y>");
          } else {
            return onIntegerTabComplete("X", 256, 0, args[args.length - 1]);
          }
        }

        if (args.length == 4) {
          if (args[args.length - 1].isEmpty()) {
            if (sendPlayer != null) {
              Block block = sendPlayer.getTargetBlock(10);
              if (block != null && !block.getType().isAir()) {
                return Collections.singletonList(block.getLocation().getBlockZ() + "");
              } else {
                return Collections.singletonList(sendPlayer.getLocation().getBlockZ() + "");
              }
            }
            return Collections.singletonList("<Z>");
          } else {
            return onIntegerTabComplete("Z", 30000000, -30000000, args[args.length - 1]);
          }
        }

        if (args.length == 5) {
          List<String> list = new ArrayList<>();
          return autoComplete(Tool.Lista.worldNames(), args[args.length - 1]);
        }

        int commandArgsLength = 5;
        if (args.length <= commandArgsLength + 4) {
          List<String> list = new ArrayList<>(
              Arrays.asList("-silent", "-s", "-remove-air", "-remove-water", "-remove-lava"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
              if (arg.equals("-remove-air")) {
                list.remove("-remove-air");
              }
              if (arg.equals("-remove-water")) {
                list.remove("-remove-water");
              }
              if (arg.equals("-remove-lava")) {
                list.remove("-remove-lava");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("rotate")) {
        if (!sender.hasPermission("cherry.wand.rotate")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Arrays.asList("right", "left"), args[args.length - 1]);
        }

        int commandArgsLength = 2;
        if (args.length <= commandArgsLength + 1) {
          List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("flip")) {
        if (!sender.hasPermission("cherry.wand.flip")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Arrays.asList("east", "west", "south", "north", "up", "down"), args[args.length - 1]);
        }

        int commandArgsLength = 2;
        if (args.length <= commandArgsLength + 1) {
          List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("move")) {
        if (!sender.hasPermission("cherry.wand.move")) {
          return Collections.emptyList();
        }

        // length
        if (args.length == 2) {
          return onIntegerTabComplete("거리", 100000, 1, args[args.length - 1]);
        }

        if (args.length == 3) {
          return autoComplete(Arrays.asList("east", "west", "south", "north", "up", "down"), args[args.length - 1]);
        }

        int commandArgsLength = 3;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("stack")) {
        if (!sender.hasPermission("cherry.wand.stack")) {
          return Collections.emptyList();
        }

        // repeat
        if (args.length == 2) {
          return onIntegerTabComplete("반복 횟수", 1000, 1, args[args.length - 1]);
        }

        if (args.length == 3) {
          return autoComplete(Arrays.asList("east", "west", "south", "north", "up", "down"), args[args.length - 1]);
        }

        int commandArgsLength = 3;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("replace")) {
        if (!sender.hasPermission("cherry.wand.replace")) {
          return Collections.emptyList();
        }

        if (args.length == 2 || args.length == 3) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        int commandArgsLength = 3;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("replacenear")) {
        if (!sender.hasPermission("cherry.wand.replacenear")) {
          return Collections.emptyList();
        }

        if (args.length == 2 || args.length == 3) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        // radius
        if (args.length == 4) {
          return onIntegerTabComplete("반지름", 100000, 1, args[args.length - 1]);
        }

        int commandArgsLength = 4;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("cube") || args[0].equals("emptycube") || args[0].equals("walledcube")
          || args[0].equals("ecube") || args[0].equals("wcube")) {
        if (!sender.hasPermission("cherry.wand.edit.cube")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        int commandArgsLength = 2;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("cyl") || args[0].equals("emptycyl") || args[0].equals("walledcyl") || args[0].equals("ecyl")
          || args[0].equals("wcyl") || args[0].equals("pointcyl") || args[0].equals("emptypointcyl")
          || args[0].equals("walledpointcyl") || args[0].equals("pcyl") || args[0].equals("epcyl")
          || args[0].equals("wpcyl")) {
        if (!sender.hasPermission("cherry.wand.edit.cyl")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        // radius
        if (args.length == 3) {
          return onIntegerTabComplete("반지름", 100000, 1, args[args.length - 1]);
        }

        // height
        if (args.length == 4) {
          return onIntegerTabComplete("높이", 100000, 1, args[args.length - 1]);
        }

        // 옵션 입력
        int commandArgsLength = 4;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("sphere") || args[0].equals("emptysphere") || args[0].equals("esphere")
          || args[0].equals("pointsphere") || args[0].equals("emptypointsphere") || args[0].equals("psphere")
          || args[0].equals("epsphere")) {
        if (sender.hasPermission("cherry.wand.edit.sphere")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        // radius
        if (args.length == 3) {
          return onIntegerTabComplete("반지름", 100000, 1, args[args.length - 1]);
        }

        // 옵션 입력
        int commandArgsLength = 3;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("wall")) {
        if (!sender.hasPermission("cherry.wand.edit.cube")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
        }

        int commandArgsLength = 2;
        if (args.length <= commandArgsLength + 2) {
          List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap", "-silent", "-s"));
          int n = 0;
          for (String arg : args) {
            if (n >= commandArgsLength) {
              if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
                list.remove("-applyPhysics");
                list.remove("-ap");
              }
              if (arg.equals("-silent") || arg.equals("-s")) {
                list.remove("-silent");
                list.remove("-s");
              }
            }
            n++;
          }
          return autoComplete(list, args[args.length - 1]);
        }
        return Collections.emptyList();
      }

      if (args[0].equals("cmdscan")) {
        if (!sender.hasPermission("cherry.wand.cmdscan")) {
          return Collections.emptyList();
        }

        if (args.length == 2) {
          return onIntegerTabComplete("반지름", 100000, 1, args[args.length - 1]);
        }
      }

    }

    /*
     * else if (command.getName().equalsIgnoreCase("wandbrush")) {
     * 
     * if (args.length == 1) {
     * List<String> list = new ArrayList<>();
     * if (sender.hasPermission("cherry.wand.brush.touch")) {
     * list.addAll(Arrays.asList("touch", "t"));
     * }
     * if (sender.hasPermission("cherry.wand.brush.setbrush")) {
     * list.addAll(Arrays.asList("brush", "b"));
     * }
     * if (sender.hasPermission("cherry.wand.brush.setradius")) {
     * list.addAll(Arrays.asList("radius", "r"));
     * }
     * if (sender.hasPermission("cherry.wand.brush.setblockdata")) {
     * list.addAll(Arrays.asList("block", "v"));
     * }
     * return autoComplete(list, args[args.length - 1]);
     * }
     * 
     * args[0] = args[0].toLowerCase();
     * 
     * switch (args[0]) {
     * 
     * case "t": {
     * }
     * case "touch": {
     * if (!sender.hasPermission("cherry.wand.brush.touch")) {
     * return Collections.emptyList();
     * }
     * 
     * if (args.length == 2) {
     * if (args[args.length - 1].isEmpty()) {
     * if (sendPlayer != null) {
     * Block block = sendPlayer.getTargetBlock(10);
     * if (block != null && !block.getType().isAir()) {
     * return Collections.singletonList(block.getLocation().getBlockX() + "");
     * }
     * else {
     * return Collections.singletonList(sendPlayer.getLocation().getBlockX() + "");
     * }
     * }
     * return Collections.singletonList("<X>");
     * }
     * else {
     * int v = 0;
     * if (sendPlayer != null) {
     * v = (int) sendPlayer.getLocation().getX();
     * }
     * return onIntegerTabCompleteWave("X", 30000000, -30000000, args[args.length -
     * 1], v);
     * }
     * }
     * 
     * if (args.length == 3) {
     * if (args[args.length - 1].isEmpty()) {
     * if (sendPlayer != null) {
     * Block block = sendPlayer.getTargetBlock(10);
     * if (block != null && !block.getType().isAir()) {
     * return Collections.singletonList(block.getLocation().getBlockY() + "");
     * }
     * else {
     * return Collections.singletonList(sendPlayer.getLocation().getBlockY() + "");
     * }
     * }
     * return Collections.singletonList("<Y>");
     * }
     * else {
     * int v = 0;
     * if (sendPlayer != null) {
     * v = (int) sendPlayer.getLocation().getY();
     * }
     * return onIntegerTabCompleteWave("Y", 256, 0, args[args.length - 1], v);
     * }
     * }
     * 
     * if (args.length == 4) {
     * if (args[args.length - 1].isEmpty()) {
     * if (sendPlayer != null) {
     * Block block = sendPlayer.getTargetBlock(10);
     * if (block != null && !block.getType().isAir()) {
     * return Collections.singletonList(block.getLocation().getBlockZ() + "");
     * }
     * else {
     * return Collections.singletonList(sendPlayer.getLocation().getBlockZ() + "");
     * }
     * }
     * return Collections.singletonList("<Z>");
     * }
     * else {
     * int v = 0;
     * if (sendPlayer != null) {
     * v = (int) sendPlayer.getLocation().getZ();
     * }
     * return onIntegerTabCompleteWave("Z", 30000000, -30000000, args[args.length -
     * 1], v);
     * }
     * }
     * 
     * if (args.length == 5) {
     * List<String> list = new ArrayList<>();
     * return autoComplete(Tool.Lista.worldNames(), args[args.length - 1]);
     * }
     * 
     * int commandArgsLength = 5;
     * if (args.length <= commandArgsLength + 2) {
     * List<String> list = new ArrayList<>(Arrays.asList("-applyPhysics", "-ap",
     * "-silent", "-s"));
     * int n = 0;
     * for (String arg : args) {
     * if (n >= commandArgsLength) {
     * if (arg.equals("-applyPhysics") || arg.equals("-ap")) {
     * list.remove("-applyPhysics");
     * list.remove("-ap");
     * }
     * if (arg.equals("-silent") || arg.equals("-s")) {
     * list.remove("-silent");
     * list.remove("-s");
     * }
     * }
     * n++;
     * }
     * return autoComplete(list, args[args.length - 1]);
     * }
     * 
     * return Collections.emptyList();
     * }
     * 
     * case "b": {
     * }
     * case "brush": {
     * if (!sender.hasPermission("cherry.wand.brush.setbrush")) {
     * return Collections.emptyList();
     * }
     * 
     * if (args.length == 2) {
     * return Arrays.asList("", "");
     * }
     * 
     * int commandArgsLength = 2;
     * if (args.length <= commandArgsLength + 1) {
     * List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
     * int n = 0;
     * for (String arg : args) {
     * if (n >= commandArgsLength) {
     * if (arg.equals("-silent") || arg.equals("-s")) {
     * list.remove("-silent");
     * list.remove("-s");
     * }
     * }
     * n++;
     * }
     * return autoComplete(list, args[args.length - 1]);
     * }
     * 
     * return Collections.emptyList();
     * }
     * 
     * case "r": {
     * }
     * case "radius": {
     * if (!sender.hasPermission("cherry.wand.brush.setradius")) {
     * return Collections.emptyList();
     * }
     * 
     * if (args.length == 2) {
     * return onIntegerTabComplete("RADIUS", 10, 0, args[args.length - 1]);
     * }
     * 
     * int commandArgsLength = 2;
     * if (args.length <= commandArgsLength + 1) {
     * List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
     * int n = 0;
     * for (String arg : args) {
     * if (n >= commandArgsLength) {
     * if (arg.equals("-silent") || arg.equals("-s")) {
     * list.remove("-silent");
     * list.remove("-s");
     * }
     * }
     * n++;
     * }
     * return autoComplete(list, args[args.length - 1]);
     * }
     * 
     * return Collections.emptyList();
     * }
     * 
     * case "v": {
     * }
     * case "block": {
     * if (!sender.hasPermission("cherry.wand.brush.setblockdata")) {
     * return Collections.emptyList();
     * }
     * 
     * if (args.length == 2) {
     * return autoComplete(Tool.Lista.materialBlocks(), args[args.length - 1]);
     * }
     * 
     * int commandArgsLength = 2;
     * if (args.length <= commandArgsLength + 1) {
     * List<String> list = new ArrayList<>(Arrays.asList("-silent", "-s"));
     * int n = 0;
     * for (String arg : args) {
     * if (n >= commandArgsLength) {
     * if (arg.equals("-silent") || arg.equals("-s")) {
     * list.remove("-silent");
     * list.remove("-s");
     * }
     * }
     * n++;
     * }
     * return autoComplete(list, args[args.length - 1]);
     * }
     * 
     * return Collections.emptyList();
     * }
     * 
     * }
     * 
     * }
     */

    return Collections.emptyList();
  }

  /*
   * private List<String> onBlock(String str) {
   * Pattern p = Pattern.compile("([\\w_]+)(\\[([\\w=_,]+)\\])?");
   * Matcher m = p.matcher(str);
   * 
   * List<String> returnValue = new ArrayList<>();
   * 
   * if (m.find()) {
   * 
   * String name = m.group(1);
   * Material material = Material.valueOf(name);
   * List<String> AvailableBlockDataKeys =
   * Arrays.asList(BlockDataInfo.getBlockDataKeys(material));
   * 
   * String blockData = m.group(2);
   * 
   * if (blockData == null) {
   * for(String key : AvailableBlockDataKeys) {
   * returnValue.add(name + "[" + key + "=");
   * }
   * return returnValue;
   * }
   * else {
   * String[] blockDataArray = blockData.split(",");
   * for(String data : blockDataArray) {
   * String[] kv = data.split("=");
   * String key = kv[0];
   * String val = kv[1];
   * AvailableBlockDataKeys.remove(key);
   * }
   * }
   * }
   * 
   * }
   */

  private List<String> onIntegerTabComplete(String name, int max, int min, String input) {
    if (input.isEmpty()) {
      return Collections.singletonList("<" + name.replaceAll(" ", "_") + ">");
    } else {
      try {
        int i = Integer.parseInt(input);
        if (i > max) {
          return Collections.singletonList("최대 " + name + " 값은 " + max + "입니다.");
        }
        if (i < min) {
          return Collections.singletonList("최소 " + name + " 값은 " + min + "입니다.");
        }
      } catch (Exception e) {
        if (DataTypeChecker.isInteger(input)) {
          return Collections.singletonList("프로그램 상 사용할 수 없는 범위의 수입니다.");
        } else {
          return Collections.singletonList(name + " 값은 정수만 입력할 수 있습니다.");
        }
      }
      return Collections.singletonList(input);
    }
  }

  private List<String> onIntegerTabCompleteWave(String name, int max, int min, String input, int v) {
    if (input.isEmpty()) {
      return Collections.singletonList("<" + name.replaceAll(" ", "_") + ">");
    } else {
      try {
        int i = Integer.parseInt(input);
        if (i > max) {
          return Collections.singletonList("최대 " + name + " 값은 " + max + "입니다.");
        }
        if (i < min) {
          return Collections.singletonList("최소 " + name + " 값은 " + min + "입니다.");
        }
      } catch (Exception e) {
        if (input.startsWith("~")) {
          if (input.equals("~")) {
            return Collections.singletonList(input);
          }
          try {
            int i = Integer.parseInt(input.substring(1));
            if (v < 0) {
              v--;
            }
            if (v + i > max) {
              return Collections.singletonList("최대 ~" + name + " 값은 ~" + (max - v) + "입니다.");
            }
            if (v + i < min) {
              return Collections.singletonList("최소 ~" + name + " 값은 ~" + (min + v) + "입니다.");
            }
            return Collections.singletonList(input);
          } catch (Exception ex) {
            if (DataTypeChecker.isInteger(input.substring(1))) {
              return Collections.singletonList("프로그램 상 사용할 수 없는 범위의 수입니다.");
            }
          }
        }

        if (DataTypeChecker.isInteger(input)) {
          return Collections.singletonList("프로그램 상 사용할 수 없는 범위의 수입니다.");
        } else {
          return Collections.singletonList(name + " 값은 정수만 입력할 수 있습니다.");
        }
      }
      return Collections.singletonList(input);
    }
  }

}
