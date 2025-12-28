package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.items.VampirismItemBloodFoodItem;
import net.daanlokdrog.vampirismthemasquerade.HeartBloodFoodWitness;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.daanlokdrog.vampirismthemasquerade.BloodOverlayMarker;
import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.teamlapen.vampirism.util.Helper;

@Mixin(VampirismItemBloodFoodItem.class)
public abstract class VampirismItemBloodFoodItemMixin {

    @Inject(
        method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("TAIL")
    )
    private void onFinishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (!level.isClientSide && entityLiving instanceof Player player) {
            if (Helper.isVampire(player)) {
                HeartBloodFoodWitness.handleWitness(player);
                if (player instanceof ServerPlayer serverPlayer) {
                    BloodOverlayMarker.markBloodied(serverPlayer, 60);
                }
            }
        }
    }
}
