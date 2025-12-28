package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerBloodCountMixin {

    @Unique
    private int bloodFeedCount = 0;
    @Unique
    private int lastFeedVictimId = -1;
    @Unique
    private Player actualPlayer;

    @Shadow private int feed_victim;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"))
    private void vampirismthemasquerade$storePlayerInstance(Player player, CallbackInfo ci) {
        this.actualPlayer = player;
    }

    private boolean isMasqueradeAdvancedVampire() {
        if (this.actualPlayer == null) return false;
        return VampirismPlayerAttributes.get(this.actualPlayer).vampireLevel >= 7;
    }

    private boolean isGenericCreature(Entity victim) {
        return victim instanceof LivingEntity && !(victim instanceof Villager) && !(victim instanceof Player);
    }

    @Inject(method = "drinkBlood", at = @At("HEAD"), cancellable = true)
    private void vampirismthemasquerade$applyFeedCounter(int amt, float saturationMod, boolean useRemaining, IDrinkBloodContext drinkContext, CallbackInfo ci) {
        if (!isMasqueradeAdvancedVampire()) return;

        Entity victim = this.actualPlayer.level().getEntity(this.feed_victim);
        if (victim == null || !isGenericCreature(victim)) {
            this.bloodFeedCount = 0;
            return;
        }

        if (this.feed_victim != this.lastFeedVictimId) {
            this.bloodFeedCount = 0;
            this.lastFeedVictimId = this.feed_victim;
        }

        if (amt <= 0) return;

        this.bloodFeedCount++;

        if (this.bloodFeedCount < 3) {
            if (this.actualPlayer instanceof ServerPlayer serverPlayer) {
                net.daanlokdrog.vampirismthemasquerade.BloodOverlayMarker.markBloodied(serverPlayer, 60);
            }
            ci.cancel();
        } else {
            this.bloodFeedCount = 0;
        }
    }

    @Inject(method = "endFeeding", at = @At("HEAD"))
    private void vampirismthemasquerade$resetFeedCounterOnEnd(boolean sync, CallbackInfo ci) {
        this.bloodFeedCount = 0;
        this.lastFeedVictimId = -1;
    }
}