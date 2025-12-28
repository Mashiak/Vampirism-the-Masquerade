package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import de.teamlapen.vampirism.util.Helper;

@EventBusSubscriber(value = Dist.CLIENT)
public class DunamisOverlay {

    private static final ResourceLocation DUNAMIS_1 = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis1.png");
    private static final ResourceLocation DUNAMIS_2 = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis2.png");
    private static final ResourceLocation DUNAMIS_3 = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis3.png");
    private static final ResourceLocation DUNAMIS_4 = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis4.png");
    private static final ResourceLocation DUNAMIS_5 = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/dunamis5.png");

    private static final int DRAW_W = 64; 
    private static final int DRAW_H = 64;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        if (!MasqueradeConfigConfiguration.DUNAMIS.get()) return;
        if (!Helper.isVampire(player)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        int dunamis = (int) vars.dunamis;
        int h = mc.getWindow().getGuiScaledHeight();

        int x = 40;
        int y = h - DRAW_H - 20;

        ResourceLocation bgTexture;
        if (dunamis <= 20) bgTexture = DUNAMIS_1;
        else if (dunamis <= 40) bgTexture = DUNAMIS_2;
        else if (dunamis <= 60) bgTexture = DUNAMIS_3;
        else if (dunamis <= 80) bgTexture = DUNAMIS_4;
        else bgTexture = DUNAMIS_5;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        event.getGuiGraphics().blit(bgTexture, x, y, 0, 0, DRAW_W, DRAW_H, 64, 64);

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        float t = (dunamis <= 50) ? dunamis / 50.0f : (dunamis - 50) / 50.0f;
        int startColor = (dunamis <= 50) ? 0xFFFFFF : 0x8B0000;
        int endColor = (dunamis <= 50) ? 0x8B0000 : 0xFFA500;
        int color = FastColor.ARGB32.lerp(t, startColor | 0xFF000000, endColor | 0xFF000000) & 0xFFFFFF;

        Component textComp = Component.translatable("vampirism_the_masquerade.dunamis", dunamis);
        int textWidth = mc.font.width(textComp);
        int availableWidth = DRAW_W - 12;

        float scale = textWidth > availableWidth ? Math.max(0.7f, (float) availableWidth / textWidth) : 1.0f;

        var pose = event.getGuiGraphics().pose();
        pose.pushPose();
        
        float centerX = x + DRAW_W / 2.0f;
        float centerY = y + DRAW_H / 2.0f;

        pose.translate(centerX, centerY, 0);
        pose.scale(scale, scale, 1.0f);

        event.getGuiGraphics().drawString(
            mc.font,
            textComp,
            -textWidth / 2,
            -mc.font.lineHeight / 2,
            color,
            true
        );

        pose.popPose();
    }
}