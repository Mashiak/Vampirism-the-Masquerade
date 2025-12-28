package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import net.minecraft.world.entity.PathfinderMob;
import net.daanlokdrog.vampirismthemasquerade.InfectionPanic;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.daanlokdrog.vampirismthemasquerade.util.ObserverUnmarker;

/**
 * overwriting the new entity's UUID with the original entity's UUID to ensure consistency.
 */
@Mixin(DefaultConvertingHandler.class)
public class DefaultConvertingHandlerMixin<T extends PathfinderMob> {

    @Inject(
        method = "createFrom",
        at = @At("RETURN")
    )
    private void vtm$preserveUuidAndAddPanicOnConvert(@NotNull T originalEntity, CallbackInfoReturnable<IConvertedCreature<T>> cir) {
        IConvertedCreature<T> convertedCreature = cir.getReturnValue();

        if (convertedCreature != null) {
            convertedCreature.asEntity().setUUID(originalEntity.getUUID());
            ObserverUnmarker.removeObserverMark(convertedCreature.asEntity());
            InfectionPanic.addPanicOnConversion(convertedCreature);
        }
    }
}