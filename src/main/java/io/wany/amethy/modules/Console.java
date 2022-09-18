package io.wany.amethy.modules;

import net.kyori.adventure.text.Component;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.wany.amethy.Amethy;

public class Console {

  public static void message(Component message) {
    Bukkit.getConsoleSender().sendMessage(message);
  }

  private static String objectsString(Object... objects) {
    StringBuilder message = new StringBuilder();
    for (Object object : objects) {
      if (object instanceof Component) {
        message.append(Message.stringify((Component) object));
      } else if (object instanceof String) {
        message.append((String) object);
      } else if (object instanceof Number) {
        message.append("&6" + (String) object);
      } else if (object instanceof Boolean) {
        message.append("&b" + (String) object);
      } else if (object instanceof Player) {
        message.append(((Player) object).displayName());
      } else if (object instanceof Entity) {
        message.append(((Entity) object).customName());
      } else if (object instanceof ItemStack) {
        message.append(((ItemStack) object).displayName());
      } else {
        message.append(object.toString());
      }
      message.append(" ");
    }
    return message.toString();
  }

  public static void log(Object... objects) {
    String message = objectsString(objects);
    logInfo(Color.mfc2ansi(Message.effect(Amethy.PREFIX_CONSOLE + message)) + "\u001b[0m");
  }

  public static void warn(Object... objects) {
    String message = objectsString(objects);
    logWarn(Color.mfc2ansi(Message.effect(Amethy.PREFIX_CONSOLE + message)) + "\u001b[0m");
  }

  public static void error(Object... objects) {
    String message = objectsString(objects);
    logError(Color.mfc2ansi(Message.effect(Amethy.PREFIX_CONSOLE + message)) + "\u001b[0m");
  }

  public static void fatal(Object... objects) {
    String message = objectsString(objects);
    logFatal(Color.mfc2ansi(Message.effect(Amethy.PREFIX_CONSOLE + message)) + "\u001b[0m");
  }

  public static void debug(Object... objects) {
    if (!Amethy.DEBUG) {
      return;
    }
    String message = objectsString(objects);
    logInfo(Color.mfc2ansi(Message.effect(Amethy.PREFIX_CONSOLE + "[DEBUG] " + message)) + "\u001b[0m");
  }

  private static void logInfo(String message) {
    Logger logger = (Logger) LogManager.getRootLogger();
    logger.log(Level.INFO, message, message, message);
  }

  private static void logWarn(String message) {
    Logger logger = (Logger) LogManager.getRootLogger();
    logger.log(Level.WARN, message, message, message);
  }

  private static void logError(String message) {
    Logger logger = (Logger) LogManager.getRootLogger();
    logger.log(Level.ERROR, message, message, message);
  }

  private static void logFatal(String message) {
    Logger logger = (Logger) LogManager.getRootLogger();
    logger.log(Level.FATAL, message, message, message);
  }

  private static void logDebug(String message) {
    Logger logger = (Logger) LogManager.getRootLogger();
    logger.log(Level.DEBUG, message, message, message);
  }

}
