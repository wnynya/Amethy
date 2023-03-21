package io.wany.amethy.modules;

import org.bukkit.entity.Player;

public interface ServerMessage {

  Object of(Object... objects);

  String stringify(Object object);

  Object parse(String string);

  void send(Object audience, String a1, String a2, Object... o);

  default void send(Object audience, String a1, Object... o) {
    send(audience, a1, null, o);
  }

  default void send(Object audience, Object... o) {
    send(audience, null, null, o);
  }

  default void info(Object audience, String prefix, Object... objects) {
    send(audience, prefix, objects);
  }

  default void warn(Object audience, String prefix, Object... objects) {
    send(audience, prefix, "§e§l[경고]: ", objects);
  }

  default void error(Object audience, String prefix, Object... objects) {
    send(audience, prefix, "§e§l[오류]: ", objects);
  }

  void kick(Player player, Object... o);

}
