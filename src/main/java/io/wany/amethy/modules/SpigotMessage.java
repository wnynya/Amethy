package io.wany.amethy.modules;

import io.wany.amethy.console;
import io.wany.amethy.supports.vault.VaultChat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class SpigotMessage implements ServerMessage {

  private static SpigotMessage THIS;

  public SpigotMessage() {
    THIS = this;
  }

  @Override
  public Object of(Object... objects) {
    StringBuilder builder = new StringBuilder();
    for (Object obj : objects) {
      if (obj instanceof String) {
        builder.append((String) obj);
      }
      else {
        builder.append(obj != null ? obj.toString() : "null");
      }
    }
    return builder.toString();
  }

  @Override
  public String stringify(Object object) {
    if (!(object instanceof BaseComponent[])) {
      throw new IllegalArgumentException("object must instanceof BaseComponent[]");
    }
    return ComponentSerializer.toString(object);
  }

  @Override
  public Object parse(String string) {
    return ComponentSerializer.parse(string);
  }

  @Override
  public void send(Object audience, String a1, String a2, Object... o) {
    a1 = a1 == null ? "" : a1;
    a2 = a2 == null ? "" : a2;
    Object[] objs = new Object[o.length + 2];
    objs[0] = a1;
    objs[1] = a2;
    System.arraycopy(o, 0, objs, 2, o.length);
    CommandSender sender = (CommandSender) audience;
    String string = (String) of(objs);
    if (sender instanceof ConsoleCommandSender) {
      console.log(string);
    }
    else {
      sender.sendMessage(string);
    }
  }

  @Override
  public void kick(Player player, Object... o) {
    Object[] objs = new Object[o.length];
    System.arraycopy(o, 0, objs, 0, o.length);
    player.kickPlayer((String) of(objs));
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

    private final HashMap<String, Formatter.FormattingModule> modules = new HashMap<>();

    private void module(String key, Formatter.FormattingModule module) {
      this.modules.put(key, module);
    }

    public String format(String format, Object... args) {
      format = format.replace("&", "§");
      String pendings = format;
      StringBuilder part = new StringBuilder();

      StringBuilder component = new StringBuilder();

      for (int i = 0; i < format.length(); i++) {
        if (format.charAt(i) == '{') {
          component.append(THIS.of(part.toString()));
          part = new StringBuilder();

          List<String> keys = this.modules.keySet().stream().toList();
          for (String key : keys) {
            Formatter.FormattingModule module = this.modules.get(key);

            if (find(key, pendings)) {
              component.append(module.format(args));
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

      component.append(THIS.of(part.toString()));

      return component.toString();
    }

    public static final Formatter PLAYER = new Formatter();
    public static final Formatter PLAYER_SERVER = new Formatter();
    public static final Formatter PLAYER_CHAT = new Formatter();
    public static final Formatter PLAYER_CHAT_SERVER = new Formatter();

    private interface FormattingModule {

      String format(Object[] objects);

    }

    private static class Modules {
      // 문자열
      public static final Formatter.FormattingModule STRING = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          return (String) THIS.of(objects[0]);
        }
      };
      public static final Formatter.FormattingModule $STRING = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          return (String) THIS.of(objects[1]);
        }
      };
      public static final Formatter.FormattingModule $$STRING = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          return (String) THIS.of(objects[2]);
        }
      };
      // 플레이어 이름 (args:Player)
      public static final Formatter.FormattingModule PLAYER_NAME = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          Player player = (Player) objects[0];
          return (String) THIS.of(player.getName());
        }
      };
      // 플레이어 displayName (args:Player)
      public static final Formatter.FormattingModule PLAYER_DISPLAYNAME = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          Player player = (Player) objects[0];
          return (String) THIS.of(player.getDisplayName());
        }
      };
      // 플레이어 Vault 접두사 (args:Player)
      public static final Formatter.FormattingModule PLAYER_VAULT_PREFIX = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          Player player = (Player) objects[0];
          return (String) THIS.of(VaultChat.prefix(player));
        }
      };
      // 플레이어 Vault 접미사 (args:Player)
      public static final Formatter.FormattingModule PLAYER_VAULT_SUFFIX = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          Player player = (Player) objects[0];
          return (String) THIS.of(VaultChat.suffix(player));
        }
      };
      // 플레이어 이름 (args:UUID)
      public static final Formatter.FormattingModule $UUID_PLAYER_NAME = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (String) THIS.of(player.getName());
        }
      };
      // 플레이어 Vault 접두사  (args:UUID)
      public static final Formatter.FormattingModule $UUID_PLAYER_VAULT_PREFIX = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (String) THIS.of(VaultChat.prefix(player));
        }
      };
      // 플레이어 Vault 접두사  (args:UUID)
      public static final Formatter.FormattingModule $UUID_PLAYER_VAULT_SUFFIX = new Formatter.FormattingModule() {
        @Override
        public String format(Object[] objects) {
          UUID uuid = (UUID) objects[1];
          OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
          return (String) THIS.of(VaultChat.suffix(player));
        }
      };
    }

    static {
      // Player
      PLAYER.module("name", Formatter.Modules.PLAYER_NAME);
      PLAYER.module("displayname", Formatter.Modules.PLAYER_DISPLAYNAME);
      PLAYER.module("prefix", Formatter.Modules.PLAYER_VAULT_PREFIX);
      PLAYER.module("suffix", Formatter.Modules.PLAYER_VAULT_SUFFIX);

      // Player, Component:message
      PLAYER_CHAT.module("name", Formatter.Modules.PLAYER_NAME);
      PLAYER_CHAT.module("displayname", Formatter.Modules.PLAYER_DISPLAYNAME);
      PLAYER_CHAT.module("prefix", Formatter.Modules.PLAYER_VAULT_PREFIX);
      PLAYER_CHAT.module("suffix", Formatter.Modules.PLAYER_VAULT_SUFFIX);
      PLAYER_CHAT.module("message", Modules.$STRING);

      // String:server, Component:name
      PLAYER_SERVER.module("server", Formatter.Modules.STRING);
      PLAYER_SERVER.module("name", Modules.$UUID_PLAYER_NAME);
      PLAYER_SERVER.module("displayname", Modules.$UUID_PLAYER_NAME);
      PLAYER_SERVER.module("prefix", Modules.$UUID_PLAYER_VAULT_PREFIX);
      PLAYER_SERVER.module("suffix", Modules.$UUID_PLAYER_VAULT_SUFFIX);

      // String:server, Component:name, Component:message
      PLAYER_CHAT_SERVER.module("server", Formatter.Modules.STRING);
      PLAYER_CHAT_SERVER.module("name", Modules.$UUID_PLAYER_NAME);
      PLAYER_CHAT_SERVER.module("displayname", Modules.$UUID_PLAYER_NAME);
      PLAYER_CHAT_SERVER.module("prefix", Modules.$UUID_PLAYER_VAULT_PREFIX);
      PLAYER_CHAT_SERVER.module("suffix", Modules.$UUID_PLAYER_VAULT_SUFFIX);
      PLAYER_CHAT_SERVER.module("message", Modules.$$STRING);
    }

  }

}
