package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Optional;

public class VillagerGarlicFarmGoal extends Goal {
    private static final double REACH_DIST = 2.0D;
    private static final double SPEED = 0.5D;
    private static final int FILL_QTY = 12;

    private final Villager villager;
    private BlockPos targetPos;

    public VillagerGarlicFarmGoal(Villager villager) {
        this.villager = villager;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (villager.isSleeping() || !(villager.level() instanceof ServerLevel level)) return false;
        if (!isPanicHigh(level)) return false;
        return (targetPos = findFarmland(level)) != null;
    }

    @Override
    public boolean canContinueToUse() {
        return targetPos != null && !villager.getNavigation().isDone() && isPanicHigh((ServerLevel) villager.level());
    }

    @Override
    public void start() {
        var seed = ModBlocks.GARLIC.get().asItem();
        if (villager.getInventory().countItem(seed) <= 0) {
            villager.getInventory().addItem(new ItemStack(seed, FILL_QTY));
        }
        villager.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), SPEED);
    }

    @Override
    public void tick() {
        if (targetPos == null) return;

        if (villager.distanceToSqr(targetPos.getCenter()) < REACH_DIST) {
            plantGarlic();
        } else {
            if (villager.tickCount % 20 == 0) {
                villager.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), SPEED);
            }
        }
        villager.getLookControl().setLookAt(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
    }

    private void plantGarlic() {
        if (!(villager.level() instanceof ServerLevel level) || !isPlantable(targetPos, level)) {
            targetPos = null;
            return;
        }

        var garlic = ModBlocks.GARLIC.get();
        BlockState state = garlic.defaultBlockState();

        if (!villager.getInventory().removeItemType(garlic.asItem(), 1).isEmpty()) {
            level.setBlock(targetPos.above(), state, 3);
            level.playSound(null, targetPos, state.getSoundType().getPlaceSound(), villager.getSoundSource(), 1.0F, 1.0F);
        }
        targetPos = null;
    }

    private BlockPos findFarmland(ServerLevel level) {
        BlockPos pos = villager.blockPosition();
        return BlockPos.betweenClosedStream(pos.offset(-9, -2, -9), pos.offset(9, 2, 9))
                .filter(p -> isPlantable(p, level))
                .findFirst()
                .map(BlockPos::immutable)
                .orElse(null);
    }

    private boolean isPlantable(BlockPos pos, ServerLevel level) {
        if (!level.getBlockState(pos).is(Blocks.FARMLAND)) return false;
        var state = level.getBlockState(pos.above());
        return state.isAir() || (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state));
    }

    private boolean isPanicHigh(ServerLevel level) {
        return Optional.ofNullable(MasqueradeVillageData.get(level))
                .map(d -> d.getCenterKeyForChunk(new ChunkPos(villager.blockPosition())))
                .map(key -> MasqueradeVillageData.get(level).getPanicForChunk(key) > 70)
                .orElse(false);
    }
}