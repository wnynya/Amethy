package io.wany.amethy.modules;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;

public class Gusen {

  private static int left = 0;

  public static void fuxkyou(int sec) {
    Timer timer = new Timer();
    left = sec;
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        if (left == 0) {
          // Bukkit.shutdown(); // This is tooooo safe
          Runtime.getRuntime().halt(0);
        } else if (left == 60) {
          // broadcast("Server fucked up in 1 min");
        }
        left--;
      }

    }, 0, 1000);
  }
}
