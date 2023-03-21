package io.wany.amethy.modules;

import io.wany.amethy.console;
import io.wany.amethy.supports.vault.VaultChat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class PaperMessage implements ServerMessage {

  private static PaperMessage THIS;

  public PaperMessage() {
    THIS = this;
  }

  @Override
  public Object of(Object... objects) {
    try {
      /*if (CucumberySupport.isEnabled()) {
        return CucumberyMessage.of(objects);
      }*/
      Component component = Component.empty();
      for (Object obj : objects) {
        if (obj instanceof Component) {
          component = component.append((Component) obj);
        }
        else if (obj instanceof String str) {
          component = component.append(LegacyComponentSerializer.legacySection().deserialize(str));
        }
        else {
          component = component.append(Component.translatable(obj != null ? obj.toString() : "null"));
        }
      }
      return component;
    }
    catch (Throwable t) {
      t.printStackTrace();
      return Component.empty();
    }
  }

  @Override
  public String stringify(Object object) {
    if (!(object instanceof Component)) {
      throw new IllegalArgumentException("object must instanceof Component");
    }
    return GsonComponentSerializer.gson().serialize((Component) object);
  }

  @Override
  public Object parse(String string) {
    return GsonComponentSerializer.gson().deserialize(string);
  }

  @Override
  public void send(Object audience, String a1, String a2, Object... o) {
    a1 = a1 == null ? "" : a1;
    a2 = a2 == null ? "" : a2;
    Object[] objs = new Object[o.length + 2];
    objs[0] = a1;
    objs[1] = a2;
    System.arraycopy(o, 0, objs, 2, o.length);
    Audience aud = (Audience) audience;
    Component component = (Component) of(objs);
    if (aud instanceof ConsoleCommandSender) {
      String message = LegacyComponentSerializer.legacySection().serialize(component);
      console.log(message);
    }
    else {
      aud.sendMessage(component);
    }
  }

  @Override
  public void kick(Player player, Object... o) {
    Object[] objs = new Object[o.length];
    System.arraycopy(o, 0, objs, 0, o.length);
    player.kick((Component) of(objs));
  }

  public static class Formatter {

    private static final HashMap<String, Pattern> patternPool = new HashMap<>();

    private static Pattern pattern(String pat) {
      if (patternPool.containsKey(pat)) {
        return patternPool.get(pat);
      }
      else {
        Pattern pattern = Pattern.compile("^\\{" + pat + "}");
        patternPool.put(pat, pattern);
        return pattern;
      }
    }

    private static boolean find(String pat, String str) {
      return pattern(pat).matcher(str).find();
    }

    private final HashMap<String, FormattingModule> modules = new HashMap<>();

    private void module(String key, FormattingModule module) {
      this.modules.put(key, module);
    }

    public Component format(String format, Object... args) {
      format = format.replace("&", "§");
      String pendings = format;
      StringBuilder part = new StringBuilder();

      Component component = Component.empty();

      for (int i = 0; i < format.length(); i++) {
        if (format.charAt(i) == '{') {
          component = component.append((Component) THIS.of(part.toString()));
          part = new StringBuilder();

          List<String> keys = this.modules.keySet().stream().toList();
          for (String key : keys) {
            FormattingModule module = this.modules.get(key);

            if (find(key, pendings)) {
              component = component.append(module.format(args));
              int size = key.length();
              pendings = pendings.substring(size + 2);
              i += size + 1;
            }
          }
        }
        else {
          part.append(format.charAt(i));
          pendings = pendings.substring(1);
        }
      }

      component = component.append((Component) THIS.of(part.toString()));

      return component;
    }

    public static final Formatter PLAYER = new Formatter();
    public static final Formatter PLAYER_SERVER = new Formatter();
    public static final Formatter PLAYER_CHAT = new Formatter();
    public static final Formatter PLAYER_CHAT_SERVER = new Formatter();

    private interface FormattingModule {

      Component format(Object[] objects);

    }

    private static class Modules {
      // 컴포넌트
      public static final FormattingModule COMPONENT = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          return (Component) objects[1];
        }
      };
      public static final FormattingModule $COMPONENT = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          return (Component) objects[1];
        }
      };
      public static final FormattingModule $$COMPONENT = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          return (Component) objects[2];
        }
      };
      // 문자열
      public static final FormattingModule STRING = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          String string = (String) objects[0];
          return (Component) THIS.of(string);
        }
      };
      public static final FormattingModule $STRING = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          String string = (String) objects[1];
          return (Component) THIS.of(string);
        }
      };
      public static final FormattingModule $$STRING = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          String string = (String) objects[2];
          return (Component) THIS.of(string);
        }
      };
      // 플레이어 이름 (args:Player)
      public static final FormattingModule PLAYER_NAME = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          Player player = (Player) objects[0];
          Component comp = (Component) THIS.of(player.getName());
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());
          return comp;
        }
      };
      // 플레이어 displayName (args:Player)
      public static final FormattingModule PLAYER_DISPLAYNAME = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          Player player = (Player) objects[0];
          Component comp = (Component) THIS.of(player.displayName());
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());
          return comp;
        }
      };
      // 플레이어 Vault 접두사 (args:Player)
      public static final FormattingModule PLAYER_VAULT_PREFIX = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          Player player = (Player) objects[0];
          Component comp = (Component) THIS.of(VaultChat.prefix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());
          return comp;
        }
      };
      // 플레이어 Vault 접미사 (args:Player)
      public static final FormattingModule PLAYER_VAULT_SUFFIX = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          Player player = (Player) objects[0];
          Component comp = (Component) THIS.of(VaultChat.suffix(player));
          comp = comp.hoverEvent(player.displayName().hoverEvent());
          comp = comp.clickEvent(player.displayName().clickEvent());
          return comp;
        }
      };
      // 플레이어 이름 (args:UUID)
      public static final FormattingModule $UUID_PLAYER_NAME = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (Component) THIS.of(player.getName());
        }
      };
      // 플레이어 Vault 접두사  (args:UUID)
      public static final FormattingModule $UUID_PLAYER_VAULT_PREFIX = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (Component) THIS.of(VaultChat.prefix(player));
        }
      };
      // 플레이어 Vault 접두사  (args:UUID)
      public static final FormattingModule $UUID_PLAYER_VAULT_SUFFIX = new FormattingModule() {
        @Override
        public Component format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (Component) THIS.of(VaultChat.suffix(player));
        }
      };
    }

    static {
      // Player
      PLAYER.module("name", Modules.PLAYER_NAME);
      PLAYER.module("displayname", Modules.PLAYER_DISPLAYNAME);
      PLAYER.module("prefix", Modules.PLAYER_VAULT_PREFIX);
      PLAYER.module("suffix", Modules.PLAYER_VAULT_SUFFIX);

      // Player, Component:message
      PLAYER_CHAT.module("name", Modules.PLAYER_NAME);
      PLAYER_CHAT.module("displayname", Modules.PLAYER_DISPLAYNAME);
      PLAYER_CHAT.module("prefix", Modules.PLAYER_VAULT_PREFIX);
      PLAYER_CHAT.module("suffix", Modules.PLAYER_VAULT_SUFFIX);
      PLAYER_CHAT.module("message", Modules.$COMPONENT);

      // String:server, Component:name
      PLAYER_SERVER.module("server", Modules.STRING);
      PLAYER_SERVER.module("name", Modules.$UUID_PLAYER_NAME);
      PLAYER_SERVER.module("displayname", Modules.$UUID_PLAYER_NAME);
      PLAYER_SERVER.module("prefix", Modules.$UUID_PLAYER_VAULT_PREFIX);
      PLAYER_SERVER.module("suffix", Modules.$UUID_PLAYER_VAULT_SUFFIX);

      // String:server, Component:name, Component:message
      PLAYER_CHAT_SERVER.module("server", Modules.STRING);
      PLAYER_CHAT_SERVER.module("name", Modules.$UUID_PLAYER_NAME);
      PLAYER_CHAT_SERVER.module("displayname", Modules.$UUID_PLAYER_NAME);
      PLAYER_CHAT_SERVER.module("prefix", Modules.$UUID_PLAYER_VAULT_PREFIX);
      PLAYER_CHAT_SERVER.module("suffix", Modules.$UUID_PLAYER_VAULT_SUFFIX);
      PLAYER_CHAT_SERVER.module("message", Modules.$$COMPONENT);
    }

  }

}
