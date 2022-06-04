package io.wany.amethy.wand.command;

import org.bukkit.command.CommandSender;

import io.wany.amethy.wand.Wand;

public interface WandCommandTask {

  boolean run(Wand wand, CommandSender sender, String[] args);

}
