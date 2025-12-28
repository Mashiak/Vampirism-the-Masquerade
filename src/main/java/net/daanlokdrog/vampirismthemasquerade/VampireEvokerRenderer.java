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
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.AbstractIllager;

public class VampireEvokerRenderer<T extends AbstractIllager> extends RenderLayer<T, IllagerModel<T>> {

    private static final ResourceLocation OVERLAY =
        ResourceLocation.parse("vampirism_the_masquerade:textures/entity/vanilla/evoker_overlay.png");

    private static final ResourceLocation EYE_TEXTURE =
        ResourceLocation.parse("vampirism_the_masquerade:textures/entity/vanilla/evoker_eye.png");

    public VampireEvokerRenderer(RenderLayerParent<T, IllagerModel<T>> parent) {
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

        VertexConsumer eyeConsumer = buffer.getBuffer(RenderType.eyes(EYE_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, eyeConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
