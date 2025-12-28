package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import static net.daanlokdrog.vampirismthemasquerade.VillageHunterSpawner.CENTER_KEY_X_TAG;
import static net.daanlokdrog.vampirismthemasquerade.VillageHunterSpawner.CENTER_KEY_Z_TAG;

@EventBusSubscriber
public class HunterTeleporter {

    private static final String VG = "village_guard";
    private static final double MS = 50.0 * 50.0;
    private static final int CI = 20;

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity baseEntity = event.getEntity();
        if (baseEntity.level().isClientSide || !(baseEntity instanceof BasicHunterEntity hunter)) {
            return;
        }

        if (!hunter.getTags().contains(VG)) {
            return;
        }

        if (hunter.level().getGameTime() % CI != 0) {
            return;
        }

        if (!hunter.getPersistentData().contains(CENTER_KEY_X_TAG) || !hunter.getPersistentData().contains(CENTER_KEY_Z_TAG)) {
             return; 
        }

        int keyX = hunter.getPersistentData().getInt(CENTER_KEY_X_TAG);
        int keyZ = hunter.getPersistentData().getInt(CENTER_KEY_Z_TAG);

        ServerLevel level = (ServerLevel) hunter.level(); 
        BlockPos centerBlockPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, 
            new BlockPos(keyX * 16 + 8, 0, keyZ * 16 + 8)
        );

        if (hunter.blockPosition().distSqr(centerBlockPos) > MS) {
            hunter.teleportTo(centerBlockPos.getX() + 0.5, centerBlockPos.getY(), centerBlockPos.getZ() + 0.5);
            level.playSound(
                null, 
                centerBlockPos, 
                SoundEvents.ENDERMAN_TELEPORT, 
                SoundSource.HOSTILE, 
                1.0F, 
                1.0F
            );
        }
    }
}