package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.items.BloodBottleItem;
import net.daanlokdrog.vampirismthemasquerade.VillagerWitnessEvent;
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

@Mixin(BloodBottleItem.class)
public abstract class BloodBottleItemMixin {

    @Inject(method = "finishUsingItem", at = @At("TAIL"))
    private void onFinishUsingItem(ItemStack stack, Level level, LivingEntity entity,
                                   CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof Player player) {
            if (!player.level().isClientSide) {
                VillagerWitnessEvent.handleWitness(player);
                BloodOverlayMarker.markBloodied((ServerPlayer) player, 60);
            }
        }
    }
}
