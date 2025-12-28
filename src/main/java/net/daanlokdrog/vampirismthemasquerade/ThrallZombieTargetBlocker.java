package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class ThrallZombieTargetBlocker {

    @SubscribeEvent
    public static void onZombieTargetChange(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        if (!(attacker instanceof Zombie)) {
            return;
        }

        if (newTarget instanceof Villager villager) {
            if (villager.getPersistentData().getBoolean("VTM_IsThrall")) {
                event.setNewAboutToBeSetTarget(null);
            }
        }
    }
}
