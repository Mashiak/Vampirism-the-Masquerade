package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class AdvisorGoalInjector {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof AdvancedVampireEntity advisor) {

            if (advisor.getPersistentData().getBoolean("IsAdvisor")) {
                
                    advisor.goalSelector.addGoal(0, new AdvisorSleepGoal(advisor));
            }
        }
    }
}