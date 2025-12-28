package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.network.chat.Component;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HuntEventManager {

    private static final Map<UUID, ServerBossEvent> activeBars = new HashMap<>();
    private static final Map<UUID, Integer> endCountdown = new HashMap<>();

public static void startHunt(ServerPlayer player) {
    VampirismTheMasqueradeModVariables.PlayerVariables vars =
        player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

    if (vars == null || vars.huntActive) return;

    vars.huntActive = true; vars.huntValue = 0.0; vars.huntBegining = false; vars.markSyncDirty();

    ServerBossEvent bossBar = new ServerBossEvent(
    Component.translatable("vampirism_the_masquerade_hunt"), 
    BossEvent.BossBarColor.WHITE, 
    BossEvent.BossBarOverlay.PROGRESS ); 
    bossBar.addPlayer(player);
    bossBar.setProgress(0.0f);

    activeBars.put(player.getUUID(), bossBar);

}


public static void tick(ServerPlayer player) {
    VampirismTheMasqueradeModVariables.PlayerVariables vars =
        player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
    if (vars == null) return;

    if (vars.huntActive && !vars.huntBegining && vars.huntValue < 100.0) {
        double speed = MasqueradeConfigConfiguration.HUNTER_BAR_FILLING_SPEED.get();
        vars.huntValue = Math.min(100.0, vars.huntValue + speed);
        vars.markSyncDirty();

        ServerBossEvent bossBar = activeBars.get(player.getUUID());
        if (bossBar != null) {
            bossBar.setProgress((float) (vars.huntValue / 100.0));
        }

        if (vars.huntValue >= 100.0) {
            vars.huntBegining = true;
            vars.markSyncDirty();

            if (bossBar != null) {
                bossBar.setName(Component.translatable("vampirism_the_masquerade_huntBegin", player.getName()));
                bossBar.setColor(BossEvent.BossBarColor.BLUE);
            }
        }
    }

    Integer countdown = endCountdown.get(player.getUUID());
    if (countdown != null) {
        int remaining = countdown - 1;
        if (remaining <= 0) {
            endCountdown.remove(player.getUUID());
            endHunt(player);
        } else {
            endCountdown.put(player.getUUID(), remaining);
        }
    }
}

    public static void updateProgress(ServerPlayer player) {
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        ServerBossEvent bossBar = activeBars.get(player.getUUID());
        if (bossBar != null) {
            bossBar.setProgress((float) (vars.huntValue / 100.0));
        }
    }

    public static void setTitle(ServerPlayer player, String translationKey) {
        ServerBossEvent bossBar = activeBars.get(player.getUUID());
        if (bossBar != null) {
            bossBar.setName(Component.translatable(translationKey));
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars != null) {
            vars.markSyncDirty();
        }

        endCountdown.put(player.getUUID(), 100);
    }

public static void endHunt(ServerPlayer player) {
    VampirismTheMasqueradeModVariables.PlayerVariables vars =
        player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        player.displayClientMessage(
        Component.translatable("vampirism_the_masquerade_hunt_end_for_now"),
        true
    );

    if (vars != null) {
        vars.huntTimes = vars.huntTimes + 1;
        vars.huntActive = false;
        vars.huntBegining = false;
        vars.huntValue = 0.0;
        vars.exposure = 0;
        vars.huntKillCounter = 0;
        vars.huntSpawnCooldown = 0;
        vars.spawnedHunters = 0;
        vars.markSyncDirty();
    }

    ServerBossEvent bossBar = activeBars.remove(player.getUUID());
    if (bossBar != null) {
        bossBar.removePlayer(player);
    }
}

    public static ServerBossEvent getActiveBar(UUID playerId) {
        return activeBars.get(playerId);
    }

    public static void putActiveBar(UUID playerId, ServerBossEvent bossBar) {
    activeBars.put(playerId, bossBar);
}

    public static void removeActiveBar(UUID playerId) {
        activeBars.remove(playerId);
    }

}



