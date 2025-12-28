package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.util.TriState;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Renders a small subtitle line under the entity's nameplate.
 * Reads a translation key from entity persistent NBT: "vtm_subtitle"
 * Incomplete.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class SubtitleNameTagRenderer {

    private static final String SUBTITLE_NBT_KEY = "vtm_subtitle";

@SubscribeEvent
public static void onRenderNameTag(RenderNameTagEvent event) {
    Entity entity = event.getEntity();
    if (entity == null) return;

    if (!event.getContent().getString().isEmpty() && event.canRender() != TriState.FALSE) {
        String key = entity.getPersistentData().getString("vtm_subtitle");
        if (key == null || key.isEmpty()) return;

        Component subtitle = Component.translatable(key);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.0F, 10.0F, 0.0F);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        int packedLight = event.getPackedLight();
        int bgColor = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;

        int width = font.width(subtitle);
        font.drawInBatch(subtitle, -width / 2.0F, 0.0F, 0xFFFFFFFF, false,
            poseStack.last().pose(), buffer, Font.DisplayMode.SEE_THROUGH, bgColor, packedLight);

        poseStack.popPose();
    }
}
}
