/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;

public class VampirismTheMasqueradeModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, VampirismTheMasqueradeMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BAT_PARTICLE = REGISTRY.register("bat_particle", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLUE_CLOTH = REGISTRY.register("blue_cloth", () -> new SimpleParticleType(true));
}