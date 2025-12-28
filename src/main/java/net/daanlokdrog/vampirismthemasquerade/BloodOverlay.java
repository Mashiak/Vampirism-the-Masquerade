package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GameRenderer;

@EventBusSubscriber(value = Dist.CLIENT)
public class BloodOverlay {

    private static final ResourceLocation BLOOD_TEXTURE =
        ResourceLocation.parse("vampirism_the_masquerade:textures/misc/blood_stain.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            mc.player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        long now = mc.level.getGameTime();
        long expire = (long) vars.bloodiedUntil;

        if (expire <= now) return;

        long remaining = expire - now;

        float factor = Math.min(1.0f, (float) remaining / (float) 200L);
        float alpha = 0.2f + (0.6f - 0.2f) * factor;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        graphics.blit(
            BLOOD_TEXTURE,
            0,
            0,
            screenWidth,
            screenHeight,
            0,
            0,
            256,
            256,
            256,
            256
        );

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
