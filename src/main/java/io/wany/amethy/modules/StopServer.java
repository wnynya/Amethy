package io.wany.amethy.modules;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;

import io.wany.amethy.Amethy;
import net.kyori.adventure.text.Component;

public class StopServer {

  private static final ExecutorService es = Executors.newFixedThreadPool(1);
  private static final Timer t = new Timer();

  public static void onEnable() {
    if (!Amethy.CONFIG.getBoolean("reboot")) {
      return;
    }
    es.submit(() -> {
      t.schedule(new TimerTask() {
        @Override
        public void run() {
          Date date = new Date();
          Calendar c = GregorianCalendar.getInstance();
          c.setTime(date);
          String h = c.get(Calendar.HOUR_OF_DAY) + "";
          String m = c.get(Calendar.MINUTE) + "";
          String s = c.get(Calendar.SECOND) + "";
          if (h.length() == 1) {
            h = "0" + h;
          }
          if (m.length() == 1) {
            m = "0" + m;
          }
          if (s.length() == 1) {
            s = "0" + s;
          }
          String time = h + ":" + m + ":" + s;
          if (time.equals(("04:30:00"))) {
            Bukkit.broadcast(Message.of("30분 후 서버가 재시작됩니다."));
          }
          if (time.equals(("04:50:00"))) {
            Bukkit.broadcast(Message.of("10분 후 서버가 재시작됩니다."));
          }
          if (time.equals(("04:59:30"))) {
            Bukkit.broadcast(Message.of("30초 후 서버가 재시작됩니다."));
          }
          if (time.equals(("04:59:50"))) {
            Bukkit.broadcast(Message.of("10초 후 서버가 재시작됩니다."));
          }
          if (time.equals(("05:00:00"))) {
            Bukkit.getScheduler().runTaskLater(Amethy.PLUGIN, () -> {
              Bukkit.getOnlinePlayers().forEach(player -> {
                player.kick(Message.of("서버가 재시작됩니다."));
              });
            }, 0);
          }
          if (time.equals(("05:00:02"))) {
            Bukkit.getScheduler().runTaskLater(Amethy.PLUGIN, () -> {
              Bukkit.shutdown();
            }, 0);
          }
        }
      }, 0, 1000);
    });
  }

  public static void onDisable() {
    t.cancel();
    es.shutdown();
  }
}
