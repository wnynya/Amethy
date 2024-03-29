package io.wany.amethy.modules;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.wany.amethy.Amethy;
import io.wany.amethy.console;

import java.io.File;
import java.util.List;

public class YamlConfig {

  public FileConfiguration config = null;
  private final String name;
  private final File file;

  public YamlConfig(String name) {
    this.name = name;
    this.file = new File(Amethy.PLUGIN.getDataFolder() + "/" + this.name + ".yml");
    this.load();
  }

  public YamlConfig(File file) {
    this.name = file.getName();
    this.file = file;
    this.load();
  }

  public YamlConfig(YamlConfiguration yamlConfiguration) {
    this.name = yamlConfiguration.getName();
    this.file = null;
    this.config = yamlConfiguration;
  }

  public void load() {
    try {
      this.config = YamlConfiguration.loadConfiguration(this.file);
      this.config.save(this.file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean exist(String configName) {
    File configFile = new File(Amethy.PLUGIN.getDataFolder() + "/" + configName + ".yml");
    return configFile.exists();
  }

  public void save() {

    try {
      if (!(this.file.exists())) {
        this.config = YamlConfiguration.loadConfiguration(file);
      }
      this.config.save(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void set(String path, Object value) {
    this.config.set(path, value);
    save();
  }

  public File file() {
    return this.file;
  }

  public String getString(String path) {
    return this.config.getString(path);
  }

  public boolean getBoolean(String path) {
    return this.config.getBoolean(path);
  }

  public int getInt(String path) {
    return this.config.getInt(path);
  }

  public double getDouble(String path) {
    return this.config.getDouble(path);
  }

  public float getFloat(String path) {
    return (float) this.config.getDouble(path);
  }

  public long getLong(String path) {
    return this.config.getLong(path);
  }

  public List<?> getList(String path) {
    return this.config.getList(path);
  }

  /*
   * public Component getComponent(String path) {
   * if (!isString(path)) {
   * return null;
   * }
   * return Message.parse(this.getString(path));
   * }
   */

  public Location getLocation(String path) {
    return this.config.getLocation(path);
  }

  public ItemStack getItenStack(String path) {
    return this.config.getItemStack(path);
  }

  public Vector getVector(String path) {
    return this.config.getVector(path);
  }

  public boolean isString(String path) {
    return this.config.isString(path);
  }

  public boolean isBoolean(String path) {
    return this.config.isBoolean(path);
  }

  public boolean isInt(String path) {
    return this.config.isInt(path);
  }

  public boolean isDouble(String path) {
    return this.config.isDouble(path);
  }

  public boolean isLong(String path) {
    return this.config.isLong(path);
  }

  public boolean isList(String path) {
    return this.config.isList(path);
  }

  public boolean isComponent(String path) {
    return isString(path);
  }

  public boolean isLocation(String path) {
    return this.config.isLocation(path);
  }

  public boolean isItemStack(String path) {
    return this.config.isItemStack(path);
  }

  public void toggle(String path) {
    set(path, !getBoolean(path));
  }

  public String initString(String path, String value) {
    if (!this.isString(path)) {
      this.set(path, value);
    }
    return this.getString(path);
  }

  public static FileConfiguration onLoad() {
    FileConfiguration config = Amethy.PLUGIN.getConfig();
    Amethy.PLUGIN.getConfig().options().copyDefaults(true);
    Amethy.PLUGIN.saveDefaultConfig();
    if (config.getInt("version") != Amethy.YAMLCONFIG_VERSION) {
      console.warn("플러그인 콘피그 버전이 맞지 않습니다. 플러그인이 정상적으로 작동하지 않을 수 있습니다.");
      console.warn("필요 버전: " + Amethy.YAMLCONFIG_VERSION + ", 감지된 버전: " + config.getInt("version"));
    }
    return config;
  }

}
