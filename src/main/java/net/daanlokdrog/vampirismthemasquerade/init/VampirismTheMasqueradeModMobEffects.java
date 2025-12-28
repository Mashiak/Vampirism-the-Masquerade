/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.daanlokdrog.vampirismthemasquerade.potion.DevilOfTheWorldMobEffect;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;

public class VampirismTheMasqueradeModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, VampirismTheMasqueradeMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> DEVIL_OF_THE_WORLD = REGISTRY.register("devil_of_the_world", () -> new DevilOfTheWorldMobEffect());
}