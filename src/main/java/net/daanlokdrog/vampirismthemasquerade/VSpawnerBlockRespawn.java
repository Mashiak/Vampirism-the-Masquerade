package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.core.ModEntities;
import net.daanlokdrog.vampirismthemasquerade.block.VSpawnerBlock;

@EventBusSubscriber
public class VSpawnerBlockRespawn {

    @SubscribeEvent
    public static void onVampireDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof BasicVampireEntity vampire)) return;
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;

        if (level.random.nextFloat() >= 0.20f) return;

        BlockPos deathPos = vampire.blockPosition();

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);

        ChunkPos currentChunk = new ChunkPos(deathPos);
        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);

        if (villageCenterKey == null || !domainData.isDomainClaimed(villageCenterKey)) {
            return;
        }

        int radius = 8;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        BlockPos foundSpawnerPos = null;

        outer:
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    checkPos.set(deathPos.getX() + dx, deathPos.getY() + dy, deathPos.getZ() + dz);
                    BlockState state = level.getBlockState(checkPos);
                    if (state.getBlock() instanceof VSpawnerBlock) {
                        foundSpawnerPos = checkPos.immutable();
                        break outer;
                    }
                }
            }
        }

        if (foundSpawnerPos != null) {
            BasicVampireEntity newVampire = ModEntities.VAMPIRE.get().create(level);
            if (newVampire != null) {
                double x = foundSpawnerPos.getX() + 0.5;
                double y = foundSpawnerPos.getY() + 1.0;
                double z = foundSpawnerPos.getZ() + 0.5;

                newVampire.moveTo(x, y, z, level.random.nextFloat() * 360F, 0.0F);
                level.addFreshEntity(newVampire);
            }
        }
    }
}
