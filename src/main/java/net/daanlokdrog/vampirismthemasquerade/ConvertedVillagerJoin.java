package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import net.daanlokdrog.vampirismthemasquerade.util.VampireVillagerUtil;

@EventBusSubscriber
public class ConvertedVillagerJoin {

    private static final ResourceLocation CONVERTED_VILLAGER_ID =
        ResourceLocation.parse("vampirism:villager_converted");

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        boolean isConvertedClass = entity instanceof ConvertedVillagerEntity;
        boolean isConvertedId = EntityType.getKey(entity.getType()).equals(CONVERTED_VILLAGER_ID);
        boolean isMcaConverted = VampireVillagerUtil.isVampireVillager(entity);

        if (isConvertedClass || isConvertedId || isMcaConverted) {
            CompoundTag tag = entity.getPersistentData();

            if (tag.getBoolean("VTM_IsThrall")) {
                tag.remove("VTM_ThrallOwner");
                tag.remove("vtm_subtitle");
                if (!entity.hasCustomName()) {
                    entity.setCustomName(Component.literal("").withStyle(style -> style.withColor(0xFFFFFF)));
                    entity.setCustomNameVisible(false);
                }
                tag.remove("VTM_IsThrall");
            }
        }
    }
}
