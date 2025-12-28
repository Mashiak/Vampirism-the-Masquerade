package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Evoker;

import java.util.EnumSet;

public class MovingWhileCastingGoal extends Goal {
    private final Evoker evoker;
    private boolean clockwise;
    private boolean backwards;
    private int strafeTicks;

    public MovingWhileCastingGoal(Evoker evoker) {
        this.evoker = evoker;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

@Override
public boolean canUse() {
    LivingEntity target = evoker.getTarget();
    return evoker.isCastingSpell() && target != null && evoker.distanceToSqr(target) <= 36.0; 
}

@Override
public boolean canContinueToUse() {
    LivingEntity target = evoker.getTarget();
    return evoker.isCastingSpell() && target != null && evoker.distanceToSqr(target) <= 36.0;
}

    @Override
    public void start() {
        clockwise = evoker.getRandom().nextBoolean();
        backwards = false;
        strafeTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = evoker.getTarget();
        if (target == null) return;

        strafeTicks++;
        if (strafeTicks >= 20) {
            if (evoker.getRandom().nextFloat() < 0.3F) clockwise = !clockwise;
            if (evoker.getRandom().nextFloat() < 0.3F) backwards = !backwards;
            strafeTicks = 0;
        }

        float forward = backwards ? -0.5F : 0.5F;
        float sideways = clockwise ? 0.5F : -0.5F;
        evoker.getMoveControl().strafe(forward, sideways);
        evoker.lookAt(target, 30.0F, 30.0F);
    }
}
