package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModBlocks;

import java.util.List;
import java.util.UUID;

import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.FlyingBloodParticleOptions;

@EventBusSubscriber
public class ThrallBellClick {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) return;

        BlockPos pos = event.getPos();
        if (level.getBlockState(pos).getBlock() != VampirismTheMasqueradeModBlocks.THRALL_BELL.get()) return;

        if (!(level instanceof ServerLevel serverLevel)) return;
        Player player = event.getEntity();
        UUID ownerUUID = player.getUUID();

        List<Villager> thralls = serverLevel.getEntitiesOfClass(
            Villager.class,
            player.getBoundingBox().inflate(64.0),
            v -> {
                CompoundTag tag = v.getPersistentData();
                return tag.getBoolean("VTM_IsThrall")
                    && ownerUUID.toString().equals(tag.getString("VTM_ThrallOwner"));
            }
        );

        double tx = pos.getX() + 0.5;
        double ty = pos.getY() + 1.0;
        double tz = pos.getZ() + 0.5;

        for (Villager thrall : thralls) {
            thrall.teleportTo(tx, ty, tz);
        }

        serverLevel.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);

        FlyingBloodParticleOptions options = new FlyingBloodParticleOptions(
            40,
            true,
            tx, ty, tz,
            ModParticles.FLYING_BLOOD.getId(),
            1.0f
        );
        serverLevel.addParticle(options, tx, ty, tz, 0.0, 0.0, 0.0);

        event.setCanceled(true);
    }
}
