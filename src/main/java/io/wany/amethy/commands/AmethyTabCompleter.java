package io.wany.amethy.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.wany.amethyst.EventEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AmethyTabCompleter implements TabCompleter {

  private static List<String> autoComplete(List<String> list, String arg) {
    if (!arg.equalsIgnoreCase("")) {
      List<String> filtered = new ArrayList<>();
      for (String value : list) {
        if (value.toLowerCase().contains(arg.toLowerCase())) {
          filtered.add(value);
        }
      }
      return sort(filtered);
    }
    return sort(list);
  }

  private static List<String> sort(List<String> list) {
    Collections.sort(list);
    return list;
  }

  private static List<String> listOf(String... args) {
    List<String> list = new ArrayList<String>();
    for (String arg : args) {
      list.add(arg);
    }
    return list;
  }

  private static void usedFlags(String[] args, int commandArgsLength, List<String> list) {
    int n = 0;
    for (String arg : args) {
      if (n >= commandArgsLength) {
        if (arg.equals("-silent") || arg.equals("-s")) {
          list.remove("-silent");
          list.remove("-s");
        }
        if (arg.equals("-force") || arg.equals("-f")) {
          list.remove("-force");
          list.remove("-f");
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
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    String name = command.getName().toLowerCase();

    switch (name) {

      case "amethy" -> {

        // amethy ?
        if (args.length == 1) {
          List<String> list = new ArrayList<>();
          if (sender.hasPermission("amethy.command.version")) {
            list.add("version");
          }
          if (sender.hasPermission("amethy.command.reload")) {
            list.add("reload");
          }
          if (sender.hasPermission("amethy.command.debug")) {
            list.add("debug");
          }
          if (sender.hasPermission("amethy.command.updater.update")) {
            list.add("update");
          }
          if (sender.hasPermission("amethy.command.updater")) {
            list.add("updater");
          }
          return autoComplete(list, args[args.length - 1]);
        }

        // amethy [0] ?
        args[0] = args[0].toLowerCase();
        switch (args[0].toLowerCase()) {

          case "debug": {
            if (!sender.hasPermission("amethy.command.debug")) {
              return Collections.emptyList();
            }
            // amethy debug ?
            if (args.length == 2) {
              List<String> list = listOf("enable", "disable");
              return autoComplete(list, args[args.length - 1]);
            } else {
              return Collections.emptyList();
            }
          }

          case "update": {
            if (!sender.hasPermission("amethy.command.updater.update")) {
              return Collections.emptyList();
            }
            int commandArgsLength = 1;
            // flags
            List<String> flags = listOf("-force");
            if (args.length <= commandArgsLength + flags.size()) {
              List<String> list = new ArrayList<>(flags);
              usedFlags(args, commandArgsLength, list);
              return autoComplete(list, args[args.length - 1]);
            }
            return Collections.emptyList();
          }

          case "updater": {
            if (!sender.hasPermission("amethy.command.updater")) {
              return Collections.emptyList();
            }
            // amethy updater ?
            if (args.length == 2) {
              List<String> list = listOf("channel", "automation");
              return autoComplete(list, args[args.length - 1]);
            }
            // amethy updater [1] ?
            else if (args.length == 3) {
              // amethy updater channel ?
              if (args[1].toLowerCase().equals("channel")) {
                List<String> list = listOf("release", "dev");
                return autoComplete(list, args[args.length - 1]);
              }
              // amethy updater automation ?
              else if (args[1].toLowerCase().equals("automation")) {
                List<String> list = listOf("enable", "disable");
                return autoComplete(list, args[args.length - 1]);
              } else {
                return Collections.emptyList();
              }
            } else {
              return Collections.emptyList();
            }
          }

        }

      }

      case "throw" -> {

        if (args.length == 1) {
          List<String> list = Collections.singletonList("new");
          return autoComplete(list, args[args.length - 1]);
        }

        else if (args.length >= 1 && args[0].equals("new")) {

          if (args.length == 2) {
            if (sender.hasPermission("amethy.command.reload")) {
              List<String> list = Arrays.asList("UnhandledException", "NullPointerException", "StackOverflowError",
                  "ArrayIndexOutOfBoundsException", "ClassCastException", "IllegalArgumentException",
                  "ArithmeticException", "UnsupportedOperationException");
              return autoComplete(list, args[args.length - 1]);
            }
          }

        }

      }

      case "drop" -> {

        if (args.length == 1) {
          List<String> list = new ArrayList<>(List.of("true", "false"));
          return autoComplete(list, args[args.length - 1]);
        }

      }

      case "lid" -> {

        if (args.length == 1) {
          List<String> list = new ArrayList<>(List.of("toggle", "open", "close"));
          return autoComplete(list, args[args.length - 1]);
        }

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

      case "closeinventory" -> {

        if (args.length == 1) {
          List<String> list = new ArrayList<>();
          for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
          }
          return autoComplete(list, args[args.length - 1]);
        }

      }

    }

    return Collections.emptyList();
  }

}
