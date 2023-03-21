package io.wany.amethy.modules;

import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import io.wany.amethy.supports.vault.VaultSupport;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class MsgUtil {

  public static Component of(Object... objects) {
    return ComponentUtil.create(objects);
  }

  public static String stringify(Component component) {
    return GsonComponentSerializer.gson().serialize(component);
  }

  public static Component parse(String string) {
    return GsonComponentSerializer.gson().deserialize(string);
  }

  public static void send(Audience audience, String prefix, String level, Object... objects) {
    prefix = prefix == null ? "" : prefix;
    level = level == null ? "" : level;
    Object[] objs = new Object[objects.length + 2];
    objs[0] = prefix;
    objs[1] = level;
    System.arraycopy(objects, 0, objs, 2, objects.length);
    audience.sendMessage(MsgUtil.of(objs));
  }

  public static void send(Audience audience, String prefix, Object... objects) {
    send(audience, prefix, null, MsgUtil.of(objects));
  }

  public static void send(Audience audience, Object... objects) {
    send(audience, null, null, MsgUtil.of(objects));
  }

  public static void info(Audience audience, String prefix, Object... objects) {
    send(audience, prefix, objects);
  }

  public static void warn(Audience audience, String prefix, Object... objects) {
    send(audience, prefix, "§e§l[경고]: ", objects);
  }

  public static void error(Audience audience, String prefix, Object... objects) {
    send(audience, prefix, "§e§l[오류]: ", objects);
  }

  public static Pattern pattern(String pat) {
    return Pattern.compile("^\\{" + pat + "}");
  }

  public static boolean find(String pat, String str) {
    return pattern(pat).matcher(str).find();
  }

  public static Component formatPlayer(String format, Player player) {
    Component component = Component.empty();

    format = format.replace("&", "§");

    String pendings = format;
    StringBuilder part = new StringBuilder();

    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '{') {

        component = component.append(MsgUtil.of(part.toString()));
        part = new StringBuilder();

        // 플레이어 이름
        if (find("name", pendings)) {
          Component comp = MsgUtil.of(player.getName());
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 displayname
        if (find("displayname", pendings)) {
          Component comp = player.displayName();

          int size = 11;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 UUID
        if (find("uuid", pendings)) {
          Component comp = MsgUtil.of(player.getUniqueId().toString());

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접둑사
        if (find("prefix", pendings)) {
          Component comp = MsgUtil.of(VaultSupport.CHAT.getPlayerPrefix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접미사
        if (find("suffix", pendings)) {
          Component comp = MsgUtil.of(VaultSupport.CHAT.getPlayerSuffix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

      }
      else {
        part.append(format.charAt(i));
        pendings = pendings.substring(1);
      }
    }

    component = component.append(MsgUtil.of(part.toString()));
    return component;
  }

  public static Component formatAsyncPlayerChat(String format, Player player, Component message) {
    Component component = Component.empty();

    format = format.replace("&", "§");

    String pendings = new String(format);
    StringBuilder part = new StringBuilder();

    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '{') {

        component = component.append(MsgUtil.of(part.toString()));
        part = new StringBuilder();

        // 플레이어 이름
        if (find("name", pendings)) {
          Component comp = MsgUtil.of(player.getName());
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 displayname
        if (find("displayname", pendings)) {
          Component comp = player.displayName();

          int size = 11;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 UUID
        if (find("uuid", pendings)) {
          Component comp = MsgUtil.of(player.getUniqueId().toString());

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접둑사
        if (find("prefix", pendings)) {
          Component comp = MsgUtil.of(VaultSupport.CHAT.getPlayerPrefix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접미사
        if (find("suffix", pendings)) {
          Component comp = MsgUtil.of(VaultSupport.CHAT.getPlayerSuffix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 메시지
        if (find("message", pendings)) {
          Component comp = message;

          int size = 7;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

      }
      else {
        part.append(format.charAt(i));
        pendings = pendings.substring(1);
      }
    }

    component = component.append(MsgUtil.of(part.toString()));
    return component;
  }

  public static Component formatDatabaseSyncPlayerChat(String format, String server, Component name, Component message) {
    Component component = Component.empty();

    format = format.replace("&", "§");

    String pendings = new String(format);
    StringBuilder part = new StringBuilder();

    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '{') {

        component = component.append(MsgUtil.of(part.toString()));
        part = new StringBuilder();

        // 서버
        if (find("server", pendings)) {
          Component comp = MsgUtil.of(server);

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 이름
        if (find("name", pendings)) {
          Component comp = name;

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 메시지
        if (find("message", pendings)) {
          Component comp = message;

          int size = 7;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

      }
      else {
        part.append(format.charAt(i));
        pendings = pendings.substring(1);
      }
    }

    component = component.append(MsgUtil.of(part.toString()));
    return component;
  }

  public static Component formatDatabaseSyncPlayerConnection(String format, String server, Component name) {
    Component component = Component.empty();

    format = format.replace("&", "§");

    String pendings = new String(format);
    StringBuilder part = new StringBuilder();

    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '{') {

        component = component.append(MsgUtil.of(part.toString()));
        part = new StringBuilder();

        // 서버
        if (find("server", pendings)) {
          Component comp = MsgUtil.of(server);

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 이름
        if (find("name", pendings)) {
          Component comp = name;

          int size = 4;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 displayname
        if (find("displayname", pendings)) {
          Component comp = name;

          int size = 11;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접둑사
        if (find("prefix", pendings)) {
          Component comp = MsgUtil.of("");

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

        // 플레이어 Vault 접미사
        if (find("suffix", pendings)) {
          Component comp = MsgUtil.of("");

          int size = 6;
          component = component.append(comp);
          pendings = pendings.substring(size + 2);
          i += size + 1;
          continue;
        }

      }
      else {
        part.append(format.charAt(i));
        pendings = pendings.substring(1);
      }
    }

    component = component.append(MsgUtil.of(part.toString()));
    return component;
  }

}
