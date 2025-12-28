package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

public class BloodOverlayMarker {

    public static void markBloodied(ServerPlayer player, int durationSeconds) {
    	
    	if (!MasqueradeConfigConfiguration.BLOODY_FEED.get()) {
            return;
        }
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        long currentTime = player.level().getGameTime();
        long newExpireTime = currentTime + (durationSeconds * 20L);

        if ((long) vars.bloodiedUntil > currentTime) {
            vars.bloodiedUntil = newExpireTime;
        } else {
            vars.bloodiedUntil = newExpireTime;
            vars.bloodOverlayIndex = player.level().random.nextInt(6);
        }
    }

    public static boolean isBloodied(ServerPlayer player) {
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        return (long) vars.bloodiedUntil > player.level().getGameTime();
    }

    public static boolean isBloodied(AbstractClientPlayer player) {
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        long gameTime = player.level().getGameTime();
        if ((long) vars.bloodiedUntil > gameTime) {
            return true;
        } else {
            vars.bloodOverlayIndex = -1;
            return false;
        }
    }

    public static ResourceLocation getOverlay(AbstractClientPlayer player, ResourceLocation[] overlays) {
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        int idx = Math.min((int) vars.bloodOverlayIndex, overlays.length - 1);
        return overlays[idx];
    }
}
