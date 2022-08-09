package io.wany.amethy.terminal;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.management.OperatingSystemMXBean;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.help.HelpTopic;
import org.bukkit.scheduler.BukkitTask;

import io.wany.amethy.Amethy;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TerminalDashboard {

  private static int serverCurrentTPS = 0;
  public static int serverLastTPS = 0;

  private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private static BukkitTask bukkitTask1t;
  private static final Timer timer1s = new Timer();

  private static JsonObject systemInfo = null;

  public static JsonObject getSystemInfo() {
    if (systemInfo != null) {
      return systemInfo;
    }
    JsonObject object = new JsonObject();

    try {
      JsonObject system = new JsonObject();
      OperatingSystemMXBean osb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
      system.addProperty("name", osb.getName());
      system.addProperty("version", osb.getVersion());
      system.addProperty("arch", osb.getArch());
      system.addProperty("availableProcessors", osb.getAvailableProcessors());
      system.addProperty("totalMemorySize", osb.getTotalMemorySize());
      system.addProperty("committedVirtualMemorySize", osb.getCommittedVirtualMemorySize());
      object.add("system", system);
    } catch (Exception ignored) {
    }

    try {
      JsonObject user = new JsonObject();
      user.addProperty("name", System.getProperty("user.name"));
      user.addProperty("home", System.getProperty("user.home"));
      user.addProperty("dir", System.getProperty("user.dir"));
      object.add("user", user);
    } catch (Exception ignored) {
    }

    try {
      JsonObject os = new JsonObject();
      os.addProperty("version", System.getProperty("os.version"));
      os.addProperty("name", System.getProperty("os.name"));
      os.addProperty("arch", System.getProperty("os.arch"));
      object.add("os", os);
    } catch (Exception ignored) {
    }

    try {
      JsonObject java = new JsonObject();
      java.addProperty("version", System.getProperty("java.vm.version"));
      java.addProperty("runtime", System.getProperty("java.runtime.name"));
      java.addProperty("vendor", System.getProperty("java.vm.vendor"));
      java.addProperty("home", System.getProperty("java.home"));
      object.add("java", java);
    } catch (Exception ignored) {
    }

    try {
      JsonObject server = new JsonObject();
      server.addProperty("name", Bukkit.getServer().getName());
      server.addProperty("ip", Bukkit.getServer().getIp());
      server.addProperty("port", Bukkit.getServer().getPort());
      server.addProperty("maxPlayers", Bukkit.getMaxPlayers());
      server.addProperty("version", Bukkit.getServer().getVersion());
      server.addProperty("bukkitVersion", Bukkit.getServer().getBukkitVersion());
      server.addProperty("minecraftVersion", Bukkit.getServer().getMinecraftVersion());
      server.addProperty("motd", GsonComponentSerializer.gson().serialize(Bukkit.getServer().motd()));
      server.addProperty("dir",
          new File(new File(Amethy.PLUGIN.getDataFolder().getAbsoluteFile().getParent()).getParent()).getAbsolutePath()
              .replace("\\", "/"));
      object.add("server", server);
    } catch (Exception ignored) {
    }

    try {
      JsonObject network = new JsonObject();
      String ip = null;
      String hostname = null;
      try {
        ip = InetAddress.getLocalHost().toString();
        hostname = InetAddress.getLocalHost().getHostName();
      } catch (Exception ignored) {
      }
      network.addProperty("ip", ip);
      network.addProperty("hostname", hostname);
      JsonArray netInterfaces = new JsonArray();
      try {
        Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
        while (nics.hasMoreElements()) {
          NetworkInterface nic = nics.nextElement();
          Enumeration<InetAddress> addrs = nic.getInetAddresses();
          while (addrs.hasMoreElements()) {
            InetAddress addr = addrs.nextElement();
            JsonObject netInterface = new JsonObject();
            netInterface.addProperty("name", nic.getName());
            netInterface.addProperty("address", addr.getHostAddress());
            netInterfaces.add(netInterface);
          }
        }
      } catch (Exception ignored) {
      }
      network.add("interfaces", netInterfaces);
      object.add("network", network);
    } catch (Exception ignored) {
    }

    try {
      JsonArray commands = new JsonArray();
      for (HelpTopic topic : Bukkit.getHelpMap().getHelpTopics()) {
        if (!topic.getName().startsWith("/")) {
          continue;
        }
        commands.add(topic.getName().substring(1));
      }
      object.add("commands", commands);
    } catch (Exception ignored) {
    }

    systemInfo = object;
    return systemInfo;
  }

  public static JsonObject getSystemStatus() {
    JsonObject object = new JsonObject();

    try {
      Runtime r = Runtime.getRuntime();
      object.addProperty("memory-free", r.freeMemory());
      object.addProperty("memory-max", r.maxMemory());
      object.addProperty("memory-total", r.totalMemory());
      OperatingSystemMXBean osb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
      object.addProperty("cpu-system-load", osb.getCpuLoad());
      object.addProperty("cpu-process-load", osb.getProcessCpuLoad());
      object.addProperty("tps", serverLastTPS);
    } catch (Exception ignored) {
    }

    try {
      object.addProperty("players-count", Bukkit.getOnlinePlayers().size());
    } catch (Exception ignored) {
    }

    try {
      int entitiesCount = Bukkit.getScheduler().callSyncMethod(Amethy.PLUGIN, new Callable<Integer>() {
        @Override
        public Integer call() {
          int entitiesCount = 0;
          for (World world : Bukkit.getWorlds()) {
            entitiesCount += world.getEntityCount();
          }
          return entitiesCount;
        }
      }).get();
      object.addProperty("entities-count", entitiesCount);
    } catch (Exception ignored) {
    }

    return object;
  }

  public static void sendSystemInfo() {
    Terminal.event("system-info", getSystemInfo());
  }

  public static void sendSystemStatus() {
    Terminal.event("system-status", getSystemStatus());
  }

  public static void onEnable() {
    bukkitTask1t = Bukkit.getScheduler().runTaskTimer(Amethy.PLUGIN, () -> serverCurrentTPS++, 0L, 1L);
    executorService.submit(() -> {
      timer1s.schedule(new TimerTask() {
        @Override
        public void run() {
          serverLastTPS = serverCurrentTPS;
          serverCurrentTPS = 0;
          sendSystemStatus();
        }
      }, 0, 1000);
    });
  }

  public static void onDisable() {
    bukkitTask1t.cancel();
    timer1s.cancel();
    executorService.shutdownNow();
  }

}
