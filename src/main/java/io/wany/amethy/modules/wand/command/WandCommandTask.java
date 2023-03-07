package io.wany.amethy.modules.wand.command;

import org.bukkit.command.CommandSender;

import io.wany.amethy.modules.wand.Wand;

public interface WandCommandTask {

  boolean run(Wand wand, CommandSender sender, String[] args);

}
