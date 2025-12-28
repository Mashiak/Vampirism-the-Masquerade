package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

@EventBusSubscriber
public class HunterGoalInjector {

    private static final String VG = "village_guard";
    private static final String SLEEP_GOAL_ADDED_TAG = "VTM_SleepGoalAdded";

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof BasicHunterEntity hunter) {
            if (hunter.getTags().contains(VG)) {
                    hunter.goalSelector.addGoal(1, new HunterSleepGoal(hunter));
            }
        }
    }
}