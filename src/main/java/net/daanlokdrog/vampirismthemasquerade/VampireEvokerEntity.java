package net.daanlokdrog.vampirismthemasquerade.entity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.entity.ai.goals.*;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.daanlokdrog.vampirismthemasquerade.util.VampireIllagerTeleportUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.daanlokdrog.vampirismthemasquerade.MovingWhileCastingGoal;
import net.daanlokdrog.vampirismthemasquerade.BlackSmokeEscapeGoal;

public class VampireEvokerEntity extends Evoker implements IVampireMob {

    private long lastTeleportTime = 0L;
    private boolean blackSmokeEscaping = false;
    private float recentDamage = 0F;
    private long damageWindowStart = 0L;
    private boolean forcedEscapeRequested = false;

    public VampireEvokerEntity(EntityType<? extends Evoker> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Evoker.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(5, new BiteNearbyEntityVampireGoal<>(this));
        this.goalSelector.addGoal(7, new MoveToBiteableVampireGoal<>(this, 0.75));
        this.goalSelector.addGoal(1, new MovingWhileCastingGoal(this));
        this.goalSelector.addGoal(1, new BlackSmokeEscapeGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, HunterBaseEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, VampireBaseEntity.class, true));
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            if (VampireIllagerTeleportUtil.tryTeleportNearTarget(this, target, lastTeleportTime)) {
                lastTeleportTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBlackSmokeEscaping()) {
            return false;
        }
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide) {
            long now = this.level().getGameTime();
            if (now - damageWindowStart > 300) {
                damageWindowStart = now;
                recentDamage = 0F;
            }
            recentDamage += amount;
            float halfMaxHealth = this.getMaxHealth() / 2.0F;
            if (recentDamage >= halfMaxHealth) {
                this.forcedEscapeRequested = true;
                damageWindowStart = now;
                recentDamage = 0F;
            }
        }
        return result;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (!this.blackSmokeEscaping && this.isInvisible()) {
                this.setInvisible(false);
            }
        }
    }

    public boolean consumeForcedEscapeRequested() {
        boolean r = this.forcedEscapeRequested;
        this.forcedEscapeRequested = false;
        return r;
    }

    public boolean isBlackSmokeEscaping() {
        return blackSmokeEscaping;
    }

    public void setBlackSmokeEscaping(boolean escaping) {
        this.blackSmokeEscaping = escaping;
    }

    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forceRefresh) {
        return EnumStrength.NONE;
    }

    @Override
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        return this.level().isDay() && this.level().canSeeSky(this.blockPosition());
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        return false;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }
}
