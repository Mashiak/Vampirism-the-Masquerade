package net.daanlokdrog.vampirismthemasquerade.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow protected abstract void renderTwoHandedMap(PoseStack pose, MultiBufferSource buf,
                                                       int light, float playerPitch,
                                                       float swingHeight, float swingProgress);

    @Shadow protected abstract void renderOneHandedMap(PoseStack pose, MultiBufferSource buf,
                                                       int light, float swingHeight,
                                                       HumanoidArm arm, float swingProgress,
                                                       ItemStack stack);

    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void injectNewspaperAsMap(
            AbstractClientPlayer player,
            float partialTicks,
            float playerPitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float swingHeight,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            CallbackInfo ci
    ) {
        if (stack.getItem() == VampirismTheMasqueradeModItems.NEWSPAPER_ITEM.get()) {
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            HumanoidArm arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();

            pose.pushPose();
            if (mainHand && this.offHandItem.isEmpty()) {
                this.renderTwoHandedMap(pose, buffers, light, playerPitch, swingHeight, swingProgress);
            } else {
                this.renderOneHandedMap(pose, buffers, light, swingHeight, arm, swingProgress, stack);
            }
            pose.popPose();

            ci.cancel();
        }
    }
}

