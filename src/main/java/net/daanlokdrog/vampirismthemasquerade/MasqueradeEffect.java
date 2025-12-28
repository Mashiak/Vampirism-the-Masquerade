package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

import net.daanlokdrog.vampirismthemasquerade.potion.DevilOfTheWorldMobEffect;

public class MasqueradeEffect {
    public static final String MODID = "vampirism_the_masquerade";

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MobEffect> DEVIL_OF_THE_WORLD =
        MOB_EFFECTS.register("devil_of_the_world", DevilOfTheWorldMobEffect::new);
}
