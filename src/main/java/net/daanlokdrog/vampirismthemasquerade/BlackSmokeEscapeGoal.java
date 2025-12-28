package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.entity.VampireEvokerEntity;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BlackSmokeEscapeGoal extends Goal {
    private final VampireEvokerEntity evoker;
    private LivingEntity attacker;
    private int cooldown = 0;
    private int timer = 0;

    public BlackSmokeEscapeGoal(VampireEvokerEntity evoker) {
        this.evoker = evoker;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        attacker = evoker.getLastHurtByMob();
        return attacker != null && (evoker.consumeForcedEscapeRequested() || evoker.getRandom().nextFloat() < 0.9F);
    }

    @Override
    public void start() {
        evoker.setBlackSmokeEscaping(true);
        evoker.setInvisible(true);
        timer = 20;
        cooldown = 200;
        evoker.level().playSound(null, evoker.blockPosition(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), evoker.getSoundSource(), 1.5F, 1.0F);
    }

    @Override
    public void tick() {
        if (attacker == null || !(evoker.level() instanceof ServerLevel world)) return;

        world.sendParticles(ParticleTypes.LARGE_SMOKE, evoker.getX(), evoker.getY(0.5), evoker.getZ(), 12, 0.4, 0.4, 0.4, 0.0);

        Vec3 pos = evoker.position();
        Vec3 targetPos = attacker.position();
        Vec3 diff = new Vec3(pos.x - targetPos.x, 0, pos.z - targetPos.z);

        if (diff.lengthSqr() > 0.0001) {
            Vec3 dir = diff.normalize();
            double speed = evoker.getAttributeValue(Attributes.MOVEMENT_SPEED) * 3.3;
            Vec3 dest = pos.add(dir.scale(Math.min(4.0, diff.length())));
            evoker.getNavigation().moveTo(dest.x, dest.y, dest.z, speed);
        }

        if (--timer <= 0) {
            evoker.setBlackSmokeEscaping(false);
            evoker.setInvisible(false);
        }
    }

    @Override
    public void stop() {
        if (attacker != null && attacker.isAlive()) evoker.setTarget(attacker);
        attacker = null;
        evoker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3));
    }

    @Override
    public boolean canContinueToUse() {
        return timer > 0 && attacker != null && attacker.isAlive();
    }
}