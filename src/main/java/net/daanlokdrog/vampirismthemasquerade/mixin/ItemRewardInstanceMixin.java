package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import net.daanlokdrog.vampirismthemasquerade.DonationReward;
import de.teamlapen.vampirism.util.Helper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * DonationReward
 */
@Mixin(ItemReward.Instance.class)
public class ItemRewardInstanceMixin {

    @Inject(method = "applyReward", at = @At("TAIL"), remap = false)
    private void masquerade$injectDonationReward(IFactionPlayer<?> player, CallbackInfo ci) {
        if (Helper.isVampire(player.getRepresentingPlayer())) {
            DonationReward.applyInjectedReward(player);
        }
    }
}
