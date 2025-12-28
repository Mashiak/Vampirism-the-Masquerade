package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import net.daanlokdrog.vampirismthemasquerade.DonationReward;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * DonationReward
 */
@Mixin(targets = "de.teamlapen.vampirism.entity.player.tasks.reward.RefinementItemReward$Instance", remap = false)
public abstract class RefinementItemRewardInstanceMixin implements ITaskRewardInstance {
	
    @Inject(method = "applyReward", at = @At("TAIL"), remap = false)
    private void masquerade$injectDonationReward(IFactionPlayer<?> player, CallbackInfo ci) {
        DonationReward.applyInjectedReward(player);
    }
}