package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class AdvisorLordSync {
    private static final String LORD_UUID = "VTM_LordUUID";

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AdvancedVampireEntity advisor)) return;
        if (!(advisor.level() instanceof ServerLevel level)) return;

        CompoundTag tag = advisor.getPersistentData();
        if (!tag.getBoolean("IsAdvisor")) return;

        if (advisor.tickCount % 60 != 0) return;

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);

        ChunkPos chunkKey = new ChunkPos(advisor.blockPosition());
        ChunkPos centerKey = villageData.getCenterKeyForChunk(chunkKey);
        if (centerKey == null) return;

        String domainLordUUID = domainData.getLordUUID(centerKey);
        if (domainLordUUID == null || domainLordUUID.isEmpty()) return;

        String stored = tag.getString(LORD_UUID);
        if (!domainLordUUID.equals(stored)) {
            tag.putString(LORD_UUID, domainLordUUID);
        }
    }
}
