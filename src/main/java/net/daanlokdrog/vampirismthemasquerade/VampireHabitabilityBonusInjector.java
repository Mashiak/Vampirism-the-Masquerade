package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;

@EventBusSubscriber
public class VampireHabitabilityBonusInjector {

    @SubscribeEvent
    public static void onVampireJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        boolean isRelevantVampire = 
            (entity instanceof BasicVampireEntity) ||
            (entity instanceof AdvancedVampireEntity) ||
            (entity instanceof VampireBaronEntity);

        if (isRelevantVampire) {
            VampireSpawnModifier.applyHabitabilityBonus(entity);
        }
    }
}