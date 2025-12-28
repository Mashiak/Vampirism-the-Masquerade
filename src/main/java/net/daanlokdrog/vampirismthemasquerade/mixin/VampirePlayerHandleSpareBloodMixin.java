package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerHandleSpareBloodMixin {

    @Shadow private int feed_victim; 

    @Unique
    private Player actualPlayer; 

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"))
    private void vampirismthemasquerade$storePlayerInstance(Player player, CallbackInfo ci) {
        this.actualPlayer = player;
    }

    private boolean isMasqueradeAdvancedVampire(Player player) {
        int vampireLevel = VampirismPlayerAttributes.get(player).vampireLevel;
        return (vampireLevel >= 7);
    }

    @ModifyVariable(
        method = "handleSpareBlood", 
        at = @At(
            value = "INVOKE",
            target = "Lde/teamlapen/vampirism/fluids/BloodHelper;fillBloodIntoInventory(Lnet/minecraft/world/entity/player/Player;I)I"
        ),
        ordinal = 0 
    )
    private int vampirismthemasquerade$modifySpareBloodAmount(int originalAmount) {
        Player player = this.actualPlayer; 

        if (player == null) {
            return originalAmount; 
        }

        if (!isMasqueradeAdvancedVampire(player)) {
            return originalAmount;
        }

        Entity victimEntity = player.level().getEntity(this.feed_victim);

        if (victimEntity instanceof LivingEntity && !(victimEntity instanceof Villager) && !(victimEntity instanceof Player)) {
            int newAmt = Math.max(0, Math.round((float) originalAmount / 3f));
            
            return newAmt;
        }

        return originalAmount;
    }
}