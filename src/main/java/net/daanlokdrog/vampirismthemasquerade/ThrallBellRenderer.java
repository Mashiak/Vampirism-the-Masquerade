package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class ThrallBellRenderer implements BlockEntityRenderer<BellBlockEntity> {

    private static final Material THRALL_BELL_TEXTURE =
        new Material(
            TextureAtlas.LOCATION_BLOCKS,
            ResourceLocation.parse("vampirism_the_masquerade:block/thrall_bell")
        );

    private final ModelPart bellBody;

    public ThrallBellRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.BELL);
        this.bellBody = modelpart.getChild("bell_body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();
        PartDefinition body = root.addOrReplaceChild(
            "bell_body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F),
            net.minecraft.client.model.geom.PartPose.offset(8.0F, 12.0F, 8.0F)
        );
        body.addOrReplaceChild(
            "bell_base",
            CubeListBuilder.create().texOffs(0, 13).addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F),
            net.minecraft.client.model.geom.PartPose.offset(-8.0F, -12.0F, -8.0F)
        );
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void render(BellBlockEntity bell, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float f = (float) bell.ticks + partialTicks;
        float xRot = 0.0F;
        float zRot = 0.0F;

        if (bell.shaking) {
            float shake = Mth.sin(f / (float) Math.PI) / (4.0F + f / 3.0F);
            if (bell.clickDirection == Direction.NORTH) {
                xRot = -shake;
            } else if (bell.clickDirection == Direction.SOUTH) {
                xRot = shake;
            } else if (bell.clickDirection == Direction.EAST) {
                zRot = -shake;
            } else if (bell.clickDirection == Direction.WEST) {
                zRot = shake;
            }
        }

        this.bellBody.xRot = xRot;
        this.bellBody.zRot = zRot;

        VertexConsumer vertexconsumer = THRALL_BELL_TEXTURE.buffer(bufferSource, RenderType::entitySolid);
        this.bellBody.render(poseStack, vertexconsumer, packedLight, packedOverlay);
    }
}
