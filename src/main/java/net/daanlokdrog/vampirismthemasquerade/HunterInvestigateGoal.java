package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HunterInvestigateGoal extends Goal {
    private final Mob mob;
    private final BlockPos targetPos;
    private final double speed;
    
    private int searchTimer;
    private int failTimer;
    private boolean arrived;

    public HunterInvestigateGoal(Mob mob, BlockPos target, double speed) {
        this.mob = mob;
        this.targetPos = target;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.isAlive() && !arrived && mob.getTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() == null && searchTimer <= 60 && failTimer < 100 && !arrived;
    }

    @Override
    public void start() {
        searchTimer = 0;
        failTimer = 0;
        arrived = false;
        this.move(targetPos, speed);
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        arrived = true;
    }

    @Override
    public void tick() {
        double distSqr = mob.distanceToSqr(Vec3.atCenterOf(targetPos));

        if (!arrived) {
            if (distSqr <= 4.0D) {
                arrived = true;
                mob.getNavigation().stop();
                return;
            }

            if (mob.getNavigation().isDone() || mob.getNavigation().getPath() == null) {
                if (++failTimer >= 100) arrived = true;
            } else {
                failTimer = 0;
            }

            this.move(targetPos, speed);
        } 
        
        else {
            searchTimer++;
            mob.getLookControl().setLookAt(Vec3.atCenterOf(targetPos));

            if (searchTimer % 10 == 0 && mob.getNavigation().isDone()) {
                int range = 5 + (searchTimer / 12);
                BlockPos randomPos = targetPos.offset(
                    mob.getRandom().nextInt(range * 2) - range,
                    0,
                    mob.getRandom().nextInt(range * 2) - range
                );
                this.move(randomPos, speed * 0.5);
            }
        }
    }

    private void move(BlockPos pos, double speedModifier) {
        mob.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
    }
}