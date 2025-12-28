package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock.CoffinPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.sounds.SoundSource;
import java.util.EnumSet;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class AdvisorSleepGoal extends Goal {
    private final AdvancedVampireEntity advisor;
    private BlockPos coffinPos;
    private int cooldown = 0;

    public AdvisorSleepGoal(AdvancedVampireEntity advisor) {
        this.advisor = advisor;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!advisor.getPersistentData().getBoolean("IsAdvisor") || !advisor.level().isDay()) return false;
        if (advisor.getTarget() != null && advisor.getTarget().isAlive()) return false;
        if (cooldown-- > 0) return false;

        this.coffinPos = loadClaimedCoffin();
        if (this.coffinPos == null || !isValid(coffinPos)) {
            this.coffinPos = findCoffin();
        }

        return this.coffinPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return advisor.level().isDay() && coffinPos != null && isValid(coffinPos) && advisor.getTarget() == null;
    }

    @Override
    public void start() {
        claimCoffin(coffinPos);
    }

    @Override
    public void tick() {
        if (coffinPos == null) return;

        double dist = advisor.distanceToSqr(coffinPos.getX() + 0.5, coffinPos.getY(), coffinPos.getZ() + 0.5);
        
        if (dist < 0.6) {
            if (!advisor.isSleeping()) {
                BlockState state = advisor.level().getBlockState(coffinPos);
                BlockPos headPos = getHead(coffinPos, state);
                
                if (headPos != null) {
                    advisor.getNavigation().stop();
                    advisor.setDeltaMovement(Vec3.ZERO);
                    advisor.moveTo(coffinPos.getX() + 0.5, coffinPos.getY(), coffinPos.getZ() + 0.5, advisor.getYRot(), advisor.getXRot());
                    advisor.startSleeping(headPos);
                } else {
                    stop();
                }
            } else {
                playSleepEffects();
            }
        } else {
            advisor.getNavigation().moveTo(coffinPos.getX() + 0.5, coffinPos.getY(), coffinPos.getZ() + 0.5, 1.1);
        }
    }

    @Override
    public void stop() {
        if (advisor.isSleeping()) advisor.stopSleeping();
        unclaim();
        this.coffinPos = null;
        this.cooldown = 100;
    }

    private void playSleepEffects() {
        if (advisor.level().getGameTime() % 160 == 0 && advisor.getRandom().nextFloat() < 0.3f) {
            advisor.level().playSound(null, advisor.blockPosition(), 
                advisor.getRandom().nextBoolean() ? ModSoundEvents.VAMPIRE_WIND1.get() : ModSoundEvents.VAMPIRE_WIND2.get(), 
                SoundSource.HOSTILE, 0.8F, 1.0F);
        }
    }

    private boolean isValid(BlockPos pos) {
        BlockState state = advisor.level().getBlockState(pos);
        if (!(state.getBlock() instanceof CoffinBlock)) return false;
        if (isClaimedByOther(pos)) return false;
        
        if (advisor.isSleeping() && advisor.getSleepingPos().map(p -> p.equals(getHead(pos, state))).orElse(false)) return true;
        return !state.getValue(BedBlock.OCCUPIED);
    }

    private BlockPos findCoffin() {
        if (!(advisor.level() instanceof ServerLevel sl)) return null;
        return sl.getPoiManager().findClosest(
            p -> p.is(CoffinPOIs.ADVISOR_RESTING_SPOT),
            this::isValid,
            advisor.blockPosition(), 32, PoiManager.Occupancy.HAS_SPACE
        ).map(BlockPos::immutable).orElse(null);
    }

    private BlockPos getHead(BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof CoffinBlock)) return null;
        if (state.getValue(CoffinBlock.PART) == CoffinPart.HEAD) return pos;
        Direction f = state.getValue(HORIZONTAL_FACING);
        return state.getValue(CoffinBlock.VERTICAL) ? pos.above() : pos.relative(f);
    }

    private void claimCoffin(BlockPos pos) {
        var tag = advisor.getPersistentData();
        tag.putInt("VTM_CoffinX", pos.getX());
        tag.putInt("VTM_CoffinY", pos.getY());
        tag.putInt("VTM_CoffinZ", pos.getZ());
    }

    private BlockPos loadClaimedCoffin() {
        var tag = advisor.getPersistentData();
        if (!tag.contains("VTM_CoffinX")) return null;
        return new BlockPos(tag.getInt("VTM_CoffinX"), tag.getInt("VTM_CoffinY"), tag.getInt("VTM_CoffinZ"));
    }

    private void unclaim() {
        var tag = advisor.getPersistentData();
        tag.remove("VTM_CoffinX"); tag.remove("VTM_CoffinY"); tag.remove("VTM_CoffinZ");
    }

    private boolean isClaimedByOther(BlockPos pos) {
        return !advisor.level().getEntitiesOfClass(AdvancedVampireEntity.class, new AABB(pos).inflate(16),
            e -> e != advisor && e.isAlive() && e.getPersistentData().getBoolean("IsAdvisor") && 
            pos.equals(new BlockPos(e.getPersistentData().getInt("VTM_CoffinX"), 
                                   e.getPersistentData().getInt("VTM_CoffinY"), 
                                   e.getPersistentData().getInt("VTM_CoffinZ")))
        ).isEmpty();
    }
}