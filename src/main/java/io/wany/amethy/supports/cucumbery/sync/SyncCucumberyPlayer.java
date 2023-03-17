package io.wany.amethy.supports.cucumbery.sync;

import java.util.UUID;

import org.bukkit.entity.Player;

import io.wany.amethy.modules.database.DatabaseSyncMap;
import io.wany.amethy.modules.sync.SyncPlayer;
import io.wany.amethy.modules.sync.SyncPlayerObject;
import io.wany.amethy.supports.cucumbery.CucumberySupport;

public class SyncCucumberyPlayer {

  public static void onEnable() {

    if (!CucumberySupport.isEnabled()) {
      return;
    }

    // 큐컴버리 커스텀 이펙트 동기화
    SyncPlayer.OBJECTS.add(new SyncPlayerObject() {

      @Override
      public String NAMESPACE() {
        return "player.cucumberycustomeffects";
      }

      @Override
      public String NAME() {
        return "큐컴버리 커스텀 이펙트";
      }

      @Override
      public void select(Player player) {
        if (!ENABLED()) {
          return;
        }
        UUID uuid = player.getUniqueId();
        String value = DatabaseSyncMap.getString("sync." + NAMESPACE() + "." + uuid.toString());
        if (value == null) {
          return;
        }
        StringCucumberyEffects.apply(value, player);
      }

      @Override
      public void update(Player player) {
        if (!ENABLED()) {
          return;
        }
        UUID uuid = player.getUniqueId();
        String string = StringCucumberyEffects.stringify(player);
        DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), string);
      }

    });

    // 큐컴버리 유저 데이터 동기화
    SyncPlayer.OBJECTS.add(new SyncPlayerObject() {

      @Override
      public String NAMESPACE() {
        return "player.cucumberyuserdata";
      }

      @Override
      public String NAME() {
        return "큐컴버리 유저 데이터";
      }

      @Override
      public void select(Player player) {
        if (!ENABLED()) {
          return;
        }
        UUID uuid = player.getUniqueId();
        String value = DatabaseSyncMap.getString("sync." + NAMESPACE() + "." + uuid.toString());
        if (value == null) {
          return;
        }
        StringCucumberyUserdata.apply(value, player);
      }

      @Override
      public void update(Player player) {
        if (!ENABLED()) {
          return;
        }
        UUID uuid = player.getUniqueId();
        String string = StringCucumberyUserdata.stringify(player);
        DatabaseSyncMap.set("sync." + NAMESPACE() + "." + uuid.toString(), string);
      }

    });

  }

}
