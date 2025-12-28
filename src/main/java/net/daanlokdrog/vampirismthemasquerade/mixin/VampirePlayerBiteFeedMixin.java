package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.daanlokdrog.vampirismthemasquerade.BloodOverlayMarker;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerBiteFeedMixin {

    @Unique
    private Player vtm_playerInstance;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("RETURN"),
            remap = false)
    private void vtm_storePlayerInstance(Player player, CallbackInfo ci) {
        this.vtm_playerInstance = player;
    }

    @Inject(
        method = "drinkBlood(IFZLde/teamlapen/vampirism/api/entity/player/vampire/IDrinkBloodContext;)V",
        at = @At("TAIL"),
        remap = false
    )
    private void vampirismTheMasquerade_applyBloodOverlayOnBloodDrink(
        int amt,
        float saturationMod,
        boolean useRemaining,
        IDrinkBloodContext drinkContext,
        CallbackInfo ci
    ) {
        Player player = this.vtm_playerInstance;
        if (player == null || player.level().isClientSide()) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (drinkContext != null && drinkContext.getEntity().isPresent()) {
                BloodOverlayMarker.markBloodied(serverPlayer, 60);
            }
        }
    }

    @Inject(method = "biteFeed", at = @At("TAIL"), remap = false)
    private void vampirismTheMasquerade_applyBloodOverlayOnBiteFeed(LivingEntity entity,
                                                                    CallbackInfoReturnable<Boolean> cir) {
        Player player = this.vtm_playerInstance;
        if (player == null || player.level().isClientSide()) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (!cir.getReturnValue()) {
                BloodOverlayMarker.markBloodied(serverPlayer, 60);
            }
        }
    }
}

