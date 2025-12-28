package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class VampireBaronTarget {

    @SubscribeEvent
    public static void onBaronTargetChange(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        if (!(attacker instanceof VampireBaronEntity baron)) {
            return;
        }

        if (newTarget instanceof Player player && Helper.isVampire(player)) {
            if (baron.getLastHurtByMob() != player) { 
                event.setNewAboutToBeSetTarget(null);
            }
        }
    }
}