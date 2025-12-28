package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * overwriting the new entity's UUID with the original entity's UUID to ensure consistency.
 */
@Mixin(ICurableConvertedCreature.class)
public interface ICurableConvertedCreatureMixin<T extends PathfinderMob> {

    @Inject(
        method = "createCuredEntity",
        at = @At("RETURN")
    )
    default void vtm$preserveUuidOnCure(PathfinderMob originalVampire, EntityType<T> newType, CallbackInfoReturnable<T> cir) {
        T curedVillager = cir.getReturnValue();

        if (curedVillager != null) {
            curedVillager.setUUID(originalVampire.getUUID());
        }
    }
}