package io.wany.amethy.modules;

import org.bukkit.command.CommandSender;

import io.wany.amethy.Amethy;

public interface CommandExecutor extends org.bukkit.command.CommandExecutor {

  default String prefix() {
    return Amethy.PREFIX;
  }

  default void info(CommandSender sender, Object... objects) {
    Amethy.MESSAGE.info(sender, prefix(), objects);
  }

  default void warn(CommandSender sender, Object... objects) {
    Amethy.MESSAGE.warn(sender, prefix(), objects);
  }

  default void error(CommandSender sender, Object... objects) {
    Amethy.MESSAGE.error(sender, prefix(), objects);
  }

}
