package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.ai.goals.BiteNearbyEntityVampireGoal;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiteNearbyEntityVampireGoal.class)
public abstract class BiteNearbyEntityVampireGoalMixin {

    @Inject(method = "canFeed", at = @At("HEAD"), cancellable = true)
    private void vampirismthemasquerade$excludeThralls(IExtendedCreatureVampirism entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getEntity() instanceof Villager villager) {
            if (villager.getPersistentData().getBoolean("VTM_IsThrall")) {
                cir.setReturnValue(false);
            }
        }
    }
}
