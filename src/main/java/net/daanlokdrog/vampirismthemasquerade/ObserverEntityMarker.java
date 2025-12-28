package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import net.daanlokdrog.vampirismthemasquerade.util.NoObserverUtil;

@EventBusSubscriber
public class ObserverEntityMarker {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        CompoundTag tag = living.getPersistentData();

        if (living instanceof Villager && !NoObserverUtil.isNoObserver(living)) {
            tag.putBoolean("VTM_IsObserverVillager", true);
        }

        if (living instanceof HunterBaseEntity) {
            tag.putBoolean("VTM_IsObserverHunter", true);
        }
    }
}
