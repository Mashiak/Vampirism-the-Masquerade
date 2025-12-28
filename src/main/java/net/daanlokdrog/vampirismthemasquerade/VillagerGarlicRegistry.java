package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.minecraft.world.entity.Mob;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class VillagerGarlicRegistry {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Villager villager)) {
            return;
        }
        boolean alreadyHasGoal = villager.goalSelector.getAvailableGoals().stream()
                .anyMatch(goal -> goal.getGoal() instanceof VillagerGarlicFarmGoal);

        if (!alreadyHasGoal) {
            villager.goalSelector.addGoal(5, new VillagerGarlicFarmGoal(villager));
        }
    }
}