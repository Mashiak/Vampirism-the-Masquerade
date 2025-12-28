package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.Mth;

/**
 * Charging the custom 'dunamis' variable based on the amount of blood drunk.
 */
@Mixin(VampirePlayer.class)
public abstract class BloodDrunkMixin { 

    @Unique
    private Player vtm_playerInstance;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"), remap = false)
    private void vtm_storePlayerInstance(Player player, CallbackInfo ci) {
        this.vtm_playerInstance = player;
    }

    @Inject(
        method = "drinkBlood(IFZLde/teamlapen/vampirism/api/entity/player/vampire/IDrinkBloodContext;)V",
        at = @At("TAIL"), 
        remap = false
    )
    private void vampirismTheMasquerade_chargeDunamisOnBloodDrink(
        int amt, 
        float saturationMod, 
        boolean useRemaining, 
        IDrinkBloodContext drinkContext, 
        CallbackInfo ci
    ) {
        Player player = this.vtm_playerInstance;

        if (player == null || player.level().isClientSide() || amt <= 0) {
            return;
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars = 
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars == null) {
            return;
        }

        if (vars.beast) {
           return;
        }

    double gained = amt * VReference.FOOD_TO_FLUID_BLOOD / 40.0;
    vars.dunamis = Mth.clamp(vars.dunamis + gained, 0, vars.max_dunamis);
    vars.markSyncDirty();
    }
}