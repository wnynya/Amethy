package io.wany.amethy.modules.sync;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import org.bukkit.entity.Player;

import com.jho5245.cucumbery.Initializer;

import io.wany.amethy.Amethy;

public class StringCucumberyUserdata {

  public static String stringify(Player player) {
    File file = new File(
        Amethy.PLUGINS_DIR + "/Cucumbery/data/UserData/" + player.getUniqueId().toString() + ".yml");
    try {
      return Files.readString(file.toPath());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void apply(String string, Player player) {
    File file = new File(
        Amethy.PLUGINS_DIR + "/Cucumbery/data/UserData/" + player.getUniqueId().toString() + ".yml");
    try {
      file.delete();
      file.createNewFile();
      FileWriter myWriter = new FileWriter(file);
      myWriter.write(string);
      myWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    Initializer.loadPlayerConfig(player);
  }

}
