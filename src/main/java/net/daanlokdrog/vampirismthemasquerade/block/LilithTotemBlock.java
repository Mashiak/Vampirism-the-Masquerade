package net.daanlokdrog.vampirismthemasquerade.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;

public class LilithTotemBlock extends Block {

    public LilithTotemBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(5f)
            .requiresCorrectToolForDrops());
    }

    @Override
    public int getLightBlock(BlockState state, net.minecraft.world.level.BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            ((ServerLevel) level).scheduleTick(pos, this, 60);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        AABB area = new AABB(pos).inflate(12);

        List<LivingEntity> vampires = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
            entity instanceof BasicVampireEntity ||
            entity instanceof AdvancedVampireEntity ||
            entity instanceof VampireBaronEntity
        );

        for (LivingEntity vampire : vampires) {
            vampire.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 65, 1, true, false));
        }

        level.scheduleTick(pos, this, 60);
    }
}
