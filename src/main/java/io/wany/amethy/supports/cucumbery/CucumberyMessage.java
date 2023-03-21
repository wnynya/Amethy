package io.wany.amethy.supports.cucumbery;

import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import net.kyori.adventure.text.Component;

public class CucumberyMessage {

  public static Component of(Object[] objects) {
    return ComponentUtil.create(objects);
  }

}
