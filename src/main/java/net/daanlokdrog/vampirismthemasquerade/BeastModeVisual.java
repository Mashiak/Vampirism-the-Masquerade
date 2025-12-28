package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class BeastModeVisual {
    private static final ResourceLocation BEAST_RENDER_TEXTURE =
        ResourceLocation.parse("vampirism_the_masquerade:textures/gui/beast_render.png");
    private static final ResourceLocation DUNAMIS_ICON =
        ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis3.png");
    private static final RenderType ICON_RENDER_TYPE =
        RenderType.entityCutoutNoCull(DUNAMIS_ICON);

@SubscribeEvent
public static void onRenderLivingPre(RenderLivingEvent.Pre<LivingEntity, ?> event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null) return;

    LivingEntity entity = event.getEntity();
    VampirismTheMasqueradeModVariables.PlayerVariables vars =
        mc.player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

    if (!vars.beast || entity.equals(mc.player)) return;

    event.setCanceled(true);

    PoseStack poseStack = event.getPoseStack();
    MultiBufferSource buffer = event.getMultiBufferSource();

    float width = entity.getBbWidth();
    float height = entity.getBbHeight();
    float halfWidth = width / 2.0f;
    float halfHeight = height / 2.0f;
    int packedLight = LightTexture.FULL_BRIGHT;

    poseStack.pushPose();
    poseStack.translate(0.0D, height / 2.0D, 0.0D);
    poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

    RenderType renderType = RenderType.entityTranslucent(BEAST_RENDER_TEXTURE);
    VertexConsumer consumer = buffer.getBuffer(renderType);

    consumer.addVertex(poseStack.last(), -halfWidth, halfHeight, 0.0f)
        .setUv(0.0f, 0.0f).setColor(255, 0, 0, 128)
        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
        .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

    consumer.addVertex(poseStack.last(), -halfWidth, -halfHeight, 0.0f)
        .setUv(0.0f, 1.0f).setColor(255, 0, 0, 128)
        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
        .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

    consumer.addVertex(poseStack.last(), halfWidth, -halfHeight, 0.0f)
        .setUv(1.0f, 1.0f).setColor(255, 0, 0, 128)
        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
        .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

    consumer.addVertex(poseStack.last(), halfWidth, halfHeight, 0.0f)
        .setUv(1.0f, 0.0f).setColor(255, 0, 0, 128)
        .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
        .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

    int cycle = 40;
    int window = 15;
    int phase = entity.tickCount % cycle;
    if (phase < window) {
        float t = (float)phase / (float)window;
        int alpha1 = (int)(100 * (1.0f - t)); 
        int alpha2 = (int)(60  * (1.0f - t));

        int seed = entity.getId();
        float ox = ((seed & 0x3) - 1) * 0.06f;
        float oy = (((seed >> 2) & 0x3) - 1) * 0.05f;
        float rot = (((seed >> 4) & 0x7) - 3) * 1.5f;

        poseStack.pushPose();
        poseStack.translate(ox, oy, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
        poseStack.scale(1.04f, 1.04f, 1.0f);

        VertexConsumer ghost = buffer.getBuffer(renderType);
        ghost.addVertex(poseStack.last(), -halfWidth, halfHeight, 0.0f)
            .setUv(0.0f, 0.0f).setColor(255, 0, 0, alpha1)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost.addVertex(poseStack.last(), -halfWidth, -halfHeight, 0.0f)
            .setUv(0.0f, 1.0f).setColor(255, 0, 0, alpha1)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost.addVertex(poseStack.last(), halfWidth, -halfHeight, 0.0f)
            .setUv(1.0f, 1.0f).setColor(255, 0, 0, alpha1)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost.addVertex(poseStack.last(), halfWidth, halfHeight, 0.0f)
            .setUv(1.0f, 0.0f).setColor(255, 0, 0, alpha1)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-ox * 0.8f, -oy * 0.8f, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-rot * 0.8f));
        poseStack.scale(0.98f, 0.98f, 1.0f);

        VertexConsumer ghost2 = buffer.getBuffer(renderType);
        ghost2.addVertex(poseStack.last(), -halfWidth, halfHeight, 0.0f)
            .setUv(0.0f, 0.0f).setColor(255, 0, 0, alpha2)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost2.addVertex(poseStack.last(), -halfWidth, -halfHeight, 0.0f)
            .setUv(0.0f, 1.0f).setColor(255, 0, 0, alpha2)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost2.addVertex(poseStack.last(), halfWidth, -halfHeight, 0.0f)
            .setUv(1.0f, 1.0f).setColor(255, 0, 0, alpha2)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        ghost2.addVertex(poseStack.last(), halfWidth, halfHeight, 0.0f)
            .setUv(1.0f, 0.0f).setColor(255, 0, 0, alpha2)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);
        poseStack.popPose();
    }

    poseStack.popPose();
}

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<LivingEntity, ?> event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            mc.player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        LivingEntity entity = event.getEntity();
        if (!vars.beast || entity.equals(mc.player)) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();

        float heightOffset = entity.getBbHeight() + 0.5f;
        int packedLight = LightTexture.FULL_BRIGHT;

        poseStack.pushPose();
        poseStack.translate(0.0D, heightOffset, 0.0D);
        poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        RenderSystem.disableDepthTest();

        VertexConsumer consumer = buffer.getBuffer(ICON_RENDER_TYPE);
        float halfSize = 8.0f;

        consumer.addVertex(poseStack.last(), -halfSize, halfSize, 0.0f)
            .setUv(0.0f, 0.0f).setColor(255, 255, 255, 255)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        consumer.addVertex(poseStack.last(), -halfSize, -halfSize, 0.0f)
            .setUv(0.0f, 1.0f).setColor(255, 255, 255, 255)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        consumer.addVertex(poseStack.last(), halfSize, -halfSize, 0.0f)
            .setUv(1.0f, 1.0f).setColor(255, 255, 255, 255)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        consumer.addVertex(poseStack.last(), halfSize, halfSize, 0.0f)
            .setUv(1.0f, 0.0f).setColor(255, 255, 255, 255)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
            .setNormal(poseStack.last(), 0.0f, 1.0f, 0.0f);

        RenderSystem.enableDepthTest();

        poseStack.popPose();
    }
}
