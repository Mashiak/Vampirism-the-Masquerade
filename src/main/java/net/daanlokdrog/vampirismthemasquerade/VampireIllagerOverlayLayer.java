package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractIllager;

public class VampireIllagerOverlayLayer<T extends AbstractIllager> extends RenderLayer<T, IllagerModel<T>> {

    private static final ResourceLocation OVERLAY =
        ResourceLocation.parse("vampirism_the_masquerade:textures/entity/vanilla/illager_overlay.png");

    public VampireIllagerOverlayLayer(RenderLayerParent<T, IllagerModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       T entity,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTicks,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {

                       	    if (entity.isInvisible()) {
        return;
    }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(OVERLAY));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
