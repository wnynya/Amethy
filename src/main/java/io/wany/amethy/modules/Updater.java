package io.wany.amethy.modules;

import io.wany.amethy.Amethy;
import io.wany.amethy.modules.network.HTTPRequest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;

@SuppressWarnings("all")
public class Updater {

  public static String NAME = "Updater";
  public static String COLORHEX = "#00FFC8";
  public static String COLOR = COLORHEX + ";";
  public static String PREFIX = COLOR + "&l[" + NAME + "]:&r ";

  private static final Amethy plugin = Amethy.PLUGIN;
  private static final String pluginAPI = plugin.getDescription().getAPIVersion();
  private static final String pluginVersion = plugin.getDescription().getVersion();
  private static final String userAgent = NAME;
  public static Updater defaultUpdater;

  public static class NotFoundException extends RuntimeException {
  }

  public static class InternalServerErrorException extends RuntimeException {
  }

  public static class UnknownException extends Exception {
  }

  private static final String API = Amethy.API + "/packages";
  private static final String API_KEY = "060d6031511498fd3879b21aa5d55437";
  private static String CHANNEL = "release";

  private final String api;
  private final String key;
  private final Channel channel;
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private static final Timer timer = new Timer();

  public Updater(String api, String key, Channel channel) {
    this.api = api;
    this.key = key;
    this.channel = channel;
  }

  public static Boolean isLatest() {
    Boolean latest = null;
    try {
      String url = API + "/latest?channel=" + CHANNEL + "&apiVersion=" + pluginAPI;
      JsonObject res = HTTPRequest.JSONGet(url, API_KEY);
      JsonObject data = res.get("data").getAsJsonObject();
      String version = data.get("version").getAsString();
      if (pluginVersion.equals(version)) {
        latest = true;
      } else {
        latest = false;
      }
    } catch (Exception ignored) {
    }
    return latest;
  }

  public Version getLatestVersion()
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {

    String url = this.api + "/latest?channel=" + this.channel.name + "&apiVersion=" + pluginAPI;
    JsonObject res = HTTPRequest.JSONGet(url, this.key);

    JsonObject data = res.get("data").getAsJsonObject();
    String versionID = data.get("id").getAsString();
    String versionName = data.get("version").getAsString();

    Version version = new Version(versionID, versionName);

    return version;

  }

  private File downloadVersion(Version version) throws SecurityException, IOException {
    File file = new File(plugin.getDataFolder() + "/" + version.id + ".temp");

    try {
      if (file.exists()) {
        file.delete();
      }
      file.getParentFile().mkdirs();
      file.createNewFile();
    } catch (SecurityException exception) {
      file.delete();
      throw exception;
    } catch (IOException exception) {
      file.delete();
      throw exception;
    }

    try {
      BufferedInputStream bis = new BufferedInputStream(
          new URL(API + "/" + version.name + "/download?o=" + API_KEY).openStream());
      FileOutputStream fis = new FileOutputStream(file);
      byte[] buffer = new byte[1024];
      int count = 0;
      while ((count = bis.read(buffer, 0, 1024)) != -1) {
        fis.write(buffer, 0, count);
      }
      fis.close();
      bis.close();
    } catch (SecurityException exception) {
      file.delete();
      throw exception;
    } catch (FileNotFoundException exception) {
      file.delete();
      throw exception;
    } catch (IOException exception) {
      file.delete();
      throw exception;
    }

    return file;
  }

  public void updateVersion(Version version) throws SecurityException, IOException {
    File file = downloadVersion(version);

    String name = plugin.getDescription().getName();

    File pluginsDir = new File("plugins");
    File pluginFile = new File(pluginsDir, name + "-" + version.name + ".jar");

    byte[] data = new byte[0];
    data = Files.readAllBytes(file.toPath());
    Path path = pluginFile.toPath();
    file.delete();
    Files.write(path, data);

    Bukkit.getScheduler().runTask(plugin, () -> {
      // Terminal.STATUS = Terminal.Status.UPDATE;
      PluginLoader.unload();
      if (!pluginFile.getPath().equals(Amethy.FILE.getPath())) {
        Amethy.FILE.delete();
      }
      PluginLoader.load(pluginFile);
    });
  }

  public void auto() {
    try {
      Version version = this.getLatestVersion();
      if (plugin.getDescription().getVersion().equals(version.name)) {
        return;
      }
      updateVersion(version);
    } catch (Exception ignored) {
    }
  }

  public void openAutomation() {
    this.executorService.submit(() -> {
      this.timer.schedule(new TimerTask() {
        @Override
        public void run() {
          auto();
        }
      }, 0, this.channel.checkInterval);
    });
  }

  public void closeAutomation() {
    this.timer.cancel();
    this.executorService.shutdownNow();
  }

  public String getChannelName() {
    return this.channel.name;
  }

  public static class Version {

    public final String id;
    public final String name;

    private Version(String id, String name) {
      this.id = id;
      this.name = name;
    }

  }

  public static class Channel {

    public final String name;
    public final long checkInterval;

    public Channel(String name, long checkInterval) {
      this.name = name;
      this.checkInterval = checkInterval;
    }

  }

  public static void onEnable() {
    String channelName = Amethy.CONFIG.getString("updater.channel");
    if (channelName == null) {
      channelName = "release";
    }
    CHANNEL = channelName;
    long checkInterval = 1000;
    if (channelName.equals("release")) {
      checkInterval = 1000 * 60 * 60;
    }
    Channel channel = new Channel(channelName, checkInterval);
    defaultUpdater = new Updater(API, API_KEY, channel);
    if (!Amethy.CONFIG.getBoolean("updater.auto")) {
      return;
    }
    defaultUpdater.openAutomation();
  }

  public static void onDisable() {
    defaultUpdater.closeAutomation();
  }

}
