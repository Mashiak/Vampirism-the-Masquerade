package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;

import java.util.List;

@EventBusSubscriber
public class HunterDropsRemove {

@SubscribeEvent
public static void onHunterDrops(LivingDropsEvent event) {
    LivingEntity entity = event.getEntity();

    boolean isBasic = entity instanceof BasicHunterEntity;
    boolean isAdvanced = entity instanceof AdvancedHunterEntity;
    if (!(isBasic || isAdvanced)) return;
    
    if (entity.getPersistentData().getBoolean("masquerade_spawned")) {
        event.getDrops().clear();
    }
}

}
