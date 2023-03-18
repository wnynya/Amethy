package io.wany.amethy;

import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;

public class AmethyBootstrap implements PluginBootstrap {

  @Override
  public void bootstrap(PluginProviderContext context) {
    System.out.println("################################ bootstrap ################################");
  }

  @Override
  public JavaPlugin createPlugin(PluginProviderContext context) {
    System.out.println("################################ createPlugin ################################");
    return new Amethy();
  }

}
