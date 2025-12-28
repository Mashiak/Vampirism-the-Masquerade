package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.function.Supplier;

@Mixin(SoundSource.class)
public abstract class SoundSourceMixin {

    @Shadow @Final @Mutable
    private static SoundSource[] $VALUES;

    @Invoker("<init>")
    public static SoundSource invokeNewSoundSource(String name, int ordinal, String translationKey) {
        throw new AssertionError();
    }

    private static final SoundSource VILLAGE_NOISE; 

    static {

        int newOrdinal = $VALUES.length;
        VILLAGE_NOISE = invokeNewSoundSource("VILLAGE_NOISE", newOrdinal, "village_noise");
        SoundSource[] newValues = Arrays.copyOf($VALUES, newOrdinal + 1);
        newValues[newOrdinal] = VILLAGE_NOISE;
        $VALUES = newValues;
    }
}