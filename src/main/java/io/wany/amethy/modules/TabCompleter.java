package io.wany.amethy.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface TabCompleter extends org.bukkit.command.TabCompleter {

  default List<String> autoComplete(List<String> list, String arg) {
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

  default List<String> sort(List<String> list) {
    Collections.sort(list);
    return list;
  }

  default List<String> listOf(String... args) {
    List<String> list = new ArrayList<String>();
    for (String arg : args) {
      list.add(arg);
    }
    return list;
  }

  default void usedFlags(String[] args, int commandArgsLength, List<String> list) {
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

  public class PreList {

    public static List<String> PLAYERS() {
      List<String> list = new ArrayList<>();
      for (OfflinePlayer object : Bukkit.getOfflinePlayers()) {
        list.add(object.getName());
      }
      return list;
    }

    public static List<String> PLAYERS_ONLINE() {
      List<String> list = new ArrayList<>();
      for (Player object : Bukkit.getOnlinePlayers()) {
        list.add(object.getName());
      }
      return list;
    }

    public static List<String> MATERIALS() {
      List<String> list = new ArrayList<>();
      for (Material object : Material.values()) {
        list.add(object.toString().toLowerCase());
      }
      return list;
    }

    public static List<String> WORLDS() {
      List<String> list = new ArrayList<>();
      for (World object : Bukkit.getWorlds()) {
        list.add(object.getName());
      }
      return list;
    }

  }
}
