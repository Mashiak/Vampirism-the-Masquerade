package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent; 
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber
public class DunamisDamageMultiplier {

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide()) {
            return;
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars == null) {
            return;
        }

        if (vars.beast) {
            return;
        }

        double currentDunamis = vars.dunamis;

        double multiplier = 1.0 + (currentDunamis / 100.0);
        float originalDamage = event.getNewDamage();
        float newDamage = (float) (originalDamage * multiplier);

        event.setNewDamage(newDamage);
    }
}