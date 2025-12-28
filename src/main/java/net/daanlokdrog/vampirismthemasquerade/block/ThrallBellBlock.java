package net.daanlokdrog.vampirismthemasquerade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.daanlokdrog.vampirismthemasquerade.ThrallBellBlockEntity;

import java.util.List;
import java.util.UUID;

//import net.daanlokdrog.vampirismthemasquerade.MoveToBellGoal; //not work

import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.FlyingBloodParticleOptions;

public class ThrallBellBlock extends BaseEntityBlock {

    public static final MapCodec<ThrallBellBlock> CODEC = simpleCodec(ThrallBellBlock::new);

    public ThrallBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends ThrallBellBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

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

serverLevel.sendParticles(options, tx, ty, tz, 20, 0.3, 0.6, 0.3, 0.0);

        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThrallBellBlockEntity(pos, state);
    }
}
