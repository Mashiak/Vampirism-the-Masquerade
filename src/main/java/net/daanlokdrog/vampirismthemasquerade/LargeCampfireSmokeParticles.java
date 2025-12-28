package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;

public class LargeCampfireSmokeParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY =
        DeferredRegister.create(Registries.PARTICLE_TYPE, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LARGE_CAMPFIRE_SMOKE =
        REGISTRY.register("large_campfire_smoke", () -> new SimpleParticleType(false));
}
