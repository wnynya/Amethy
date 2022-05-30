package io.wany.amethy.modules;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import io.wany.amethy.Amethy;

public class Console {

  public static void message(Component message) {
    Bukkit.getConsoleSender().sendMessage(message);
  }

  public static void log(String message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + message) + "\u001b[0m"));
  }

  public static void log(Component message) {
    Bukkit.getConsoleSender()
        .sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + Message.stringify(message)) + "\u001b[0m"));
  }

  public static void log(int message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + "&6" + message) + "\u001b[0m"));
  }

  public static void log(float message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + "&6" + message) + "\u001b[0m"));
  }

  public static void log(long message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + "&6" + message) + "\u001b[0m"));
  }

  public static void log(double message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + "&6" + message) + "\u001b[0m"));
  }

  public static void log(boolean message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + "&b" + message) + "\u001b[0m"));
  }

  public static void log(char message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + message) + "\u001b[0m"));
  }

  public static void warn(String message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + message) + "\u001b[0m"));
  }

  public static void error(String message) {
    Bukkit.getConsoleSender().sendMessage(Color.mfc2ansi(Message.effect(Prefix.AMETHY + message) + "\u001b[0m"));
  }

  public static void debug(String message) {
    if (!Amethy.DEBUG) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(
        Color.mfc2ansi(Message.effect(Prefix.AMETHY + Amethy.COLOR + Prefix.DEBUG + message) + "\u001b[0m"));
  }

  public static class Prefix {
    public static String AMETHY = "[Amethy]&r ";
    public static String DEBUG = "[Debug]:&r ";
  }

}
