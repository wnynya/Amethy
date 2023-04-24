package io.wany.amethy.modules.portal;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import io.wany.amethy.modules.TabExecutor;

public class PortalCommand implements TabExecutor {

  @Override
  public String prefix() {
    return Portal.PREFIX;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    return Collections.emptyList();
  }

}
