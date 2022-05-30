package io.wany.amethy.modules;

import io.wany.amethy.Amethy;
import io.wany.amethy.st.PluginLoader;
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
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public class Updater {

  public static String NAME = "Updater";
  public static String COLORHEX = "#00FFC8";
  public static String COLOR = COLORHEX + ";";
  public static String PREFIX = COLOR + "&l[" + NAME + "]:&r ";

  private static final Amethy plugin = Amethy.PLUGIN;
  private static final String pluginAPI = plugin.getDescription().getAPIVersion();
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

  private final Channel channel;
  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private static final Timer timer = new Timer();

  public Updater(@NotNull Channel channel) {
    this.channel = channel;
  }

  public Version getLatestVersion()
      throws IOException, ParseException, NotFoundException, InternalServerErrorException, UnknownException {

    URL url = new URL(API + "/latest?channel=" + this.channel.name + "&apiVersion=" + pluginAPI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("User-Agent", userAgent);
    connection.setRequestProperty("o", API_KEY);
    connection.setConnectTimeout(2000);
    connection.setReadTimeout(2000);

    int responseCode = connection.getResponseCode();
    if (responseCode == 200) { // OK
      Reader inputReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
      BufferedReader streamReader = new BufferedReader(inputReader);
      String streamLine;
      StringBuilder content = new StringBuilder();
      while ((streamLine = streamReader.readLine()) != null) {
        content.append(streamLine);
      }
      streamReader.close();
      connection.disconnect();
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(content.toString());
      JSONObject data = (JSONObject) object.get("data");
      String versionID = data.get("id").toString();
      String versionName = data.get("version").toString();
      return new Version(versionID, versionName);
    } else if (responseCode == 404) { // Not Found
      connection.disconnect();
      throw new NotFoundException();
    } else if (responseCode == 500) { // Internal Server Error
      connection.disconnect();
      throw new InternalServerErrorException();
    }
    connection.disconnect();
    throw new UnknownException();

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
    long checkInterval = 1000;
    if (channelName.equals("release")) {
      checkInterval = 1000 * 60 * 60;
    }
    Channel channel = new Channel(channelName, checkInterval);
    defaultUpdater = new Updater(channel);
    if (!Amethy.CONFIG.getBoolean("updater.auto")) {
      return;
    }
    defaultUpdater.openAutomation();
  }

  public static void onDisable() {
    defaultUpdater.closeAutomation();
  }

}
