package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class MoveToBellGoal extends Goal {
    private final Villager thrall;
    private final double speed;
    private BlockPos targetBell;
    private int tickCounter;

    public MoveToBellGoal(Villager thrall, double speed) {
        this.thrall = thrall;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setTargetBell(BlockPos pos) {
        this.targetBell = pos;
        this.tickCounter = 0;
    }

    @Override
    public boolean canUse() {
        return targetBell != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetBell == null) return false;

        double distSq = thrall.distanceToSqr(
            targetBell.getX() + 0.5,
            targetBell.getY() + 1.0,
            targetBell.getZ() + 0.5
        );

        if (distSq < 4.0) return false;
        return tickCounter++ < 200;
    }

    @Override
    public void stop() {
        targetBell = null;
    }

    @Override
    public void tick() {
        if (targetBell != null) {
            thrall.getNavigation().moveTo(
                targetBell.getX() + 0.5,
                targetBell.getY() + 1.0,
                targetBell.getZ() + 0.5,
                speed
            );
            thrall.getLookControl().setLookAt(
                targetBell.getX() + 0.5,
                targetBell.getY() + 1.0,
                targetBell.getZ() + 0.5,
                30.0F,
                30.0F
            );
        }
    }
}
