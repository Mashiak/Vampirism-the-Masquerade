package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.Level; 
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.nbt.CompoundTag; 
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.List;

/**
 * Let the hunters sleep during the day in the village they are stationed in.
 * It didn't works and needs further investigation.
 */

public class HunterSleepGoal extends Goal {
    private final BasicHunterEntity hunter;
    private BlockPos bedPos;
    private static final int SEARCH_RADIUS = 16;
    private static final double SLEEP_RANGE = 2.0;
    private static final String CB_X = "VTM_BedX";
    private static final String CB_Y = "VTM_BedY";
    private static final String CB_Z = "VTM_BedZ";
    private static final String VG = "village_guard";

    public HunterSleepGoal(BasicHunterEntity hunter) {
        this.hunter = hunter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!hunter.level().isDay()) {
            if (hunter.isSleeping()) {
                this.stop();
            }
            return false;
        }

        if (hunter.getTarget() != null && hunter.getTarget().isAlive()) {
            return false;
        }
        if (hunter.isSleeping()) {
            return true;
        }

        if (hunter.level().getGameTime() % 60 != 0) {
            return false; 
        }
        this.bedPos = this.findBed();
        
        if (this.bedPos != null) {
            BlockState footState = this.hunter.level().getBlockState(this.bedPos);
            if (footState.getBlock() instanceof BedBlock) {
                 BlockPos headPos = this.bedPos.relative(footState.getValue(HorizontalDirectionalBlock.FACING));
                 if (this.hunter.level().getBlockState(headPos).getValue(BedBlock.OCCUPIED)) {
                     this.unclaimBed();
                     this.bedPos = null;
                 }
            } else {
                 this.unclaimBed();
                 this.bedPos = null;
            }
        }

        return this.bedPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (hunter.isSleeping() && hunter.level().isDay()) {
            return true;
        }
        if (hunter.getTarget() != null && hunter.getTarget().isAlive()) {
             return false; 
        }

        return this.bedPos != null && hunter.level().isDay();
    }

    @Override
    public void start() {
        if (this.bedPos != null) {
            this.hunter.getNavigation().moveTo(this.bedPos.getX() + 0.5D, this.bedPos.getY() + 0.1D, this.bedPos.getZ() + 0.5D, 1.0D);
        }
    }

    @Override
    public void tick() {
        if (this.bedPos == null) return;
        if (this.hunter.blockPosition().distSqr(this.bedPos) < SLEEP_RANGE * SLEEP_RANGE) {
            BlockState footState = this.hunter.level().getBlockState(this.bedPos);
            
            if (footState.getBlock() instanceof BedBlock) {
                 if (!this.hunter.isSleeping()) {
                    this.hunter.startSleeping(this.bedPos);
                 }
            } else {
                this.unclaimBed(); 
                this.stop();
            }
            
        } else if (this.hunter.getNavigation().isDone()) {
             this.hunter.getNavigation().moveTo(this.bedPos.getX() + 0.5D, this.bedPos.getY() + 0.1D, this.bedPos.getZ() + 0.5D, 1.0D);
        }
    }

    @Override
    public void stop() {
        this.bedPos = null;
        if (hunter.isSleeping()) {
            hunter.stopSleeping(); 
        }
        this.hunter.getNavigation().stop();
        this.unclaimBed();
    }

    private BlockPos findBed() {
        BlockPos claimedPos = loadClaimedBed();
        if (claimedPos != null) {
           if (isBedStillValidAndFoot(hunter.level(), claimedPos)) {
              if (!isBedClaimedByOtherHunter(claimedPos)) {
              return claimedPos;
              }
            }
            unclaimBed();
        }

        if (!(hunter.level() instanceof ServerLevel serverLevel)) return null;

        Optional<BlockPos> headPosOpt = serverLevel.getPoiManager().findClosest(
            poiType -> poiType.is(BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolderOrThrow(PoiTypes.HOME)), 
            (pos) -> {
                BlockState state = serverLevel.getBlockState(pos);
                if (!(state.getBlock() instanceof BedBlock)) return false;
                if (state.getValue(BedBlock.PART) != BedPart.HEAD) return false;
                if (state.getValue(BedBlock.OCCUPIED)) return false; 
                BlockPos footPos = pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
                return !isBedClaimedByOtherHunter(footPos);
            },
            hunter.blockPosition(),
            SEARCH_RADIUS,
            PoiManager.Occupancy.HAS_SPACE 
        );

        if (headPosOpt.isPresent()) {
            BlockPos headPos = headPosOpt.get();
            BlockState headState = serverLevel.getBlockState(headPos);
            BlockPos footPos = headPos.relative(headState.getValue(HorizontalDirectionalBlock.FACING).getOpposite());
            claimBed(footPos);
            return footPos;
        }

        return null;
    }

    private boolean isBedStillValidAndFoot(Level level, BlockPos pos) {
         BlockState state = level.getBlockState(pos);
         if (state.getBlock() instanceof BedBlock) {
             return state.getValue(BedBlock.PART) == BedPart.FOOT;
         }
         return false;
    }

    private void claimBed(BlockPos pos) {
        CompoundTag data = hunter.getPersistentData();
        data.putInt(CB_X, pos.getX());
        data.putInt(CB_Y, pos.getY());
        data.putInt(CB_Z, pos.getZ());
    }

    private BlockPos loadClaimedBed() {
        CompoundTag data = hunter.getPersistentData();
        if (data.contains(CB_X) && data.contains(CB_Y) && data.contains(CB_Z)) {
            return new BlockPos(
                data.getInt(CB_X),
                data.getInt(CB_Y),
                data.getInt(CB_Z)
            );
        }
        return null;
    }
    
    private void unclaimBed() {
        CompoundTag data = hunter.getPersistentData();
        data.remove(CB_X);
        data.remove(CB_Y);
        data.remove(CB_Z);
    }

    private boolean isBedClaimedByOtherHunter(BlockPos pos) {
        List<BasicHunterEntity> otherGuards = hunter.level().getEntitiesOfClass(BasicHunterEntity.class, 
            new AABB(pos).inflate(20), 
            g -> g != hunter && g.isAlive() && g.getTags().contains(VG)
        );

        for (BasicHunterEntity other : otherGuards) {
            CompoundTag otherData = other.getPersistentData();
            if (otherData.contains(CB_X) && otherData.contains(CB_Y) && otherData.contains(CB_Z)) {
                BlockPos otherClaimedPos = new BlockPos(
                    otherData.getInt(CB_X),
                    otherData.getInt(CB_Y),
                    otherData.getInt(CB_Z)
                );
                if (otherClaimedPos.equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }
}