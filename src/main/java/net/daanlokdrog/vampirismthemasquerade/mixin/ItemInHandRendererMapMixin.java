package net.daanlokdrog.vampirismthemasquerade.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModItems;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMapMixin {
    private static final ResourceLocation NEWSPAPER_TEX =
        ResourceLocation.parse("vampirism_the_masquerade:textures/misc/newspaper.png");

    @Inject(method = "renderMap", at = @At("HEAD"), cancellable = true)
    private void injectRenderMap(PoseStack pose, MultiBufferSource buf, int light, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() == VampirismTheMasqueradeModItems.NEWSPAPER_ITEM.get()) {

            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
            pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
            pose.scale(0.45F, 0.45F, 0.45F);
            pose.translate(-0.55F, -0.5F, 0.0F);
            pose.scale(0.0088125F, 0.0088125F, 0.0088125F);

            VertexConsumer vc = buf.getBuffer(RenderType.text(NEWSPAPER_TEX));
            Matrix4f m = pose.last().pose();

            vc.addVertex(m, -7.0F, 135.0F, 0.0F).setColor(-1).setUv(0.0F, 1.0F).setLight(light);
            vc.addVertex(m, 135.0F, 135.0F, 0.0F).setColor(-1).setUv(1.0F, 1.0F).setLight(light);
            vc.addVertex(m, 135.0F, -7.0F, 0.0F).setColor(-1).setUv(1.0F, 0.0F).setLight(light);
            vc.addVertex(m, -7.0F, -7.0F, 0.0F).setColor(-1).setUv(0.0F, 0.0F).setLight(light);

            ci.cancel();
        }
    }
}
