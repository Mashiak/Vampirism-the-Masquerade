package net.daanlokdrog.vampirismthemasquerade.handler;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class BeastModeCombat {

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            var attackerVars = attacker.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
            if (attackerVars.beast) {
                event.setNewDamage(event.getOriginalDamage() * 4.0F);
            }
        }

        if (event.getEntity() instanceof Player victim) {
            var victimVars = victim.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
            if (victimVars.beast) {
                float reduced = event.getNewDamage() * 0.2F;
                event.setNewDamage(Math.min(reduced, 10.0F));
            }
        }
    }
}