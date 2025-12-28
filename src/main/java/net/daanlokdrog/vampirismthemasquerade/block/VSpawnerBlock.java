package net.daanlokdrog.vampirismthemasquerade.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.FlyingBloodParticleOptions;

public class VSpawnerBlock extends Block {
    public VSpawnerBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.METAL)
            .strength(5f)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

@Override
public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
    super.animateTick(state, world, pos, rand);

    if (rand.nextInt(60) == 0) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        FlyingBloodParticleOptions options = new FlyingBloodParticleOptions(
            40,
            true,
            x, y + 1.0, z,
            ModParticles.FLYING_BLOOD.getId(),
            1.0f
        );

        world.addParticle(options, x, y, z, 0.0, 0.0, 0.0);
    }
}
}
