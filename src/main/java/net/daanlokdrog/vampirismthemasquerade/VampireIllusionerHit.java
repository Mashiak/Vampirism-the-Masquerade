package net.daanlokdrog.vampirismthemasquerade.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class VampireIllusionerHit {

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        DamageSource source = event.getSource();
        if (source == null) return;
        Entity direct = source.getEntity();
        Entity trueSource = source.getDirectEntity();
        boolean fromVampireIllusioner = false;
        if (direct instanceof VampireIllusionerEntity) fromVampireIllusioner = true;
        if (!fromVampireIllusioner && trueSource instanceof VampireIllusionerEntity) fromVampireIllusioner = true;
        if (!fromVampireIllusioner) return;
        if (!(target.level() instanceof ServerLevel serverLevel)) return;
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();
        serverLevel.sendParticles(ParticleTypes.LAVA, x, y, z, 20, 0.5, 0.5, 0.5, 0.05);
        serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 30, 0.6, 0.6, 0.6, 0.02);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        serverLevel.sendParticles(ParticleTypes.FLASH, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        serverLevel.playSound(null, x, y, z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
