package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ThrallReputationBlockerMixin {

    @Inject(
        method = "onReputationEvent(Lnet/minecraft/world/entity/ai/village/ReputationEventType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/ReputationEventHandler;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void skipThrallReputation(ReputationEventType type,
                                      Entity source,
                                      ReputationEventHandler handler,
                                      CallbackInfo ci) {
        if (handler instanceof Villager villager) {
            if (villager.getPersistentData().getBoolean("VTM_IsThrall")) {
                ci.cancel();
            }
        }
    }
}
