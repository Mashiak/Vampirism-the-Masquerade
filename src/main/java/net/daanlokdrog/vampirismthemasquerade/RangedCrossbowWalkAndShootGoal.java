package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import java.util.EnumSet;

public class RangedCrossbowWalkAndShootGoal<T extends Mob & RangedAttackMob> extends Goal {
    private final T mob;
    private CrossbowState crossbowState = CrossbowState.UNCHARGED;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime;

    public RangedCrossbowWalkAndShootGoal(T mob, double speed, float attackRadius) {
        this.mob = mob;
        this.speedModifier = speed;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive() && isHoldingCrossbow();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
    }

    @Override
    public void stop() {
        mob.setAggressive(false);
        seeTime = 0;
        crossbowState = CrossbowState.UNCHARGED;
        attackDelay = 0;
        strafingTime = 0;
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
            mob.getUseItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return;

        boolean canSee = mob.getSensing().hasLineOfSight(target);
        if (canSee) {
            seeTime++;
        } else {
            seeTime = Math.max(seeTime - 1, 0);
        }

        double distSq = mob.distanceToSqr(target);

        if (canSee) {
            strafingTime++;
            if (strafingTime >= 20) {
                if (mob.getRandom().nextFloat() < 0.3F) strafingClockwise = !strafingClockwise;
                if (mob.getRandom().nextFloat() < 0.3F) strafingBackwards = !strafingBackwards;
                strafingTime = 0;
            }

            if (distSq > attackRadiusSqr * 0.75F) strafingBackwards = false;
            else if (distSq < attackRadiusSqr * 0.25F) strafingBackwards = true;

            float forward = strafingBackwards ? -0.5F : 0.5F;
            float sideways = strafingClockwise ? 0.5F : -0.5F;
            mob.getMoveControl().strafe(forward, sideways);
            mob.lookAt(target, 30.0F, 30.0F);
        } else {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (crossbowState == CrossbowState.UNCHARGED) {
            if (canSee) {
                mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(mob, item -> item instanceof CrossbowItem));
                crossbowState = CrossbowState.CHARGING;
            }
        } else if (crossbowState == CrossbowState.CHARGING) {
            if (!mob.isUsingItem()) {
                crossbowState = CrossbowState.UNCHARGED;
            } else {
                int useTicks = mob.getTicksUsingItem();
                ItemStack stack = mob.getUseItem();
                if (useTicks >= CrossbowItem.getChargeDuration(stack, mob)) {
                    mob.releaseUsingItem();
                    crossbowState = CrossbowState.CHARGED;
                    attackDelay = 5 + mob.getRandom().nextInt(10);
                }
            }
        } else if (crossbowState == CrossbowState.CHARGED) {
            attackDelay--;
            if (attackDelay <= 0) {
                crossbowState = CrossbowState.READY_TO_ATTACK;
            }
        } else if (crossbowState == CrossbowState.READY_TO_ATTACK && canSee) {
            mob.performRangedAttack(target, 1.0F);
            crossbowState = CrossbowState.UNCHARGED;
        }
    }

    enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK
    }
}
