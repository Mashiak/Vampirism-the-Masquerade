package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModMobEffects;

import net.daanlokdrog.vampirismthemasquerade.HunterSleepGoal; 

@EventBusSubscriber
public class HunterSpawnHandler {

    private static final String VG = "village_guard";

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        
        if (!(event.getEntity() instanceof BasicHunterEntity hunter)) {
            return;
        }

        hunter.goalSelector.addGoal(1, new AvoidEntityGoal<>(
            hunter,
            Player.class,
            12.0F,
            1.0D, 
            1.2D, 
            player -> player.hasEffect(VampirismTheMasqueradeModMobEffects.DEVIL_OF_THE_WORLD)
        ));
    }
}