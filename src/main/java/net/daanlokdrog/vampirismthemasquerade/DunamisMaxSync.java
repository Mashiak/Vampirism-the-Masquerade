package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModStats; 
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

import net.neoforged.neoforge.event.tick.PlayerTickEvent; 
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

@EventBusSubscriber
public class DunamisMaxSync {

    private static int updateTickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = (Player) event.getEntity(); 

        if (!MasqueradeConfigConfiguration.DUNAMIS.get()) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        if (++updateTickCounter < 20) {
            return;
        }
        updateTickCounter = 0;

        if (!Helper.isVampire(player)) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return; 
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars == null) {
            return;
        }

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int vampireLevel = atts.vampireLevel;
        int lordLevel = atts.lordLevel;

        int bloodDrunkAmount = serverPlayer.getStats().getValue(
            Stats.CUSTOM.get(ModStats.BLOOD_DRUNK.get())
        );

        int rawMaxDunamis = bloodDrunkAmount / 10000;

        int maxCap = Integer.MAX_VALUE; 

        if (vampireLevel < 7) {
            maxCap = 39;
        } else if (lordLevel == 0) {
            maxCap = 79;
        }

        int finalMaxDunamis = Math.min(rawMaxDunamis, maxCap);
        finalMaxDunamis = Math.min(finalMaxDunamis, 100);

        if (vars.max_dunamis != finalMaxDunamis) {
            vars.max_dunamis = finalMaxDunamis;

            if (vars.dunamis > finalMaxDunamis) {
                vars.dunamis = finalMaxDunamis;
            }

            vars.markSyncDirty();
        }
    }
}
