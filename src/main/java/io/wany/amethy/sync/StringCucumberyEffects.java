package io.wany.amethy.sync;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;

import io.wany.amethy.Amethy;

public class StringCucumberyEffects {

  public static String stringify(Player player) {
    CustomEffectManager.save(player.getUniqueId());
    File file = new File(
        Amethy.PLUGINS_DIR + "/Cucumbery/data/CustomEffects/" + player.getUniqueId().toString() + ".yml");
    try {
      return Files.readString(file.toPath());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void apply(YamlConfiguration yaml, Player player) {
    CustomEffectManager.load(player.getUniqueId(), yaml);
  }

  public static void apply(String string, Player player) {
    try {
      StringReader input = new StringReader(string);
      YamlConfiguration yaml = YamlConfiguration.loadConfiguration(input);
      apply(yaml, player);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

}
