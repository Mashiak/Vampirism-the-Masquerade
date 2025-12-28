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
public class BeastModeOverlay {

    private static final ResourceLocation RAGE_TEXTURE =
        ResourceLocation.parse("vampirism_the_masquerade:textures/misc/beast.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player == null) {
            return;
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars = 
            mc.player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars.beast) {
            
            double dunamis = vars.dunamis;
            float finalAlpha;

            if (dunamis >= 20.0) {
                finalAlpha = 0.50f;
            } else if (dunamis > 1.0) {
                
                double factor = (dunamis - 1.0) / (20.0 - 1.0);
                
                finalAlpha = (float) (0.05f + (0.50f - 0.05f) * factor);
            } else {
                finalAlpha = 0.05f; 
            }
            
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
            
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, finalAlpha);
            
            graphics.blit(
                RAGE_TEXTURE,
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
}