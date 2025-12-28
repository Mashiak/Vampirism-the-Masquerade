package net.daanlokdrog.vampirismthemasquerade.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ExposureOverlayOverlay {

    private static final ResourceLocation EXPOSURE_BG = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/exposure_bg.png");
    private static final ResourceLocation MASK_EXPOSURE_BG = ResourceLocation.parse("vampirism_the_masquerade:textures/gui/mask_exposure.png");

    private static final int DRAW_W = 56;
    private static final int DRAW_H = 56;
    private static final int OFFSET_RIGHT = 40;
    private static final int OFFSET_BOTTOM = 20;
    private static final int TEXT_PADDING = 6;
    private static final float MIN_SCALE = 0.7f;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui || !Helper.isVampire(player)) return;

        var vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        int exp = (int) vars.exposure;
        var gui = event.getGuiGraphics();

        int x = mc.getWindow().getGuiScaledWidth() - DRAW_W - OFFSET_RIGHT;
        int y = mc.getWindow().getGuiScaledHeight() - DRAW_H - OFFSET_BOTTOM;

        float alpha = 1.0f;
        ResourceLocation tex = EXPOSURE_BG;
        
        if (exp > 80) {
            tex = MASK_EXPOSURE_BG;
        } else if (exp <= 40) {
            alpha = 0.5f;
        }

        RenderSystem.enableBlend();
        gui.setColor(1.0f, 1.0f, 1.0f, alpha);
        gui.blit(tex, x, y, 0, 0, DRAW_W, DRAW_H, DRAW_W, DRAW_H);
        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        int color = 0xFFFFFF;
        if (exp >= 80) {
            color = (mc.level.getGameTime() / 5 % 2 == 0) ? 0xFF0000 : 0xFF5555;
        } else if (exp > 40) {
            color = 0xFFFF00;
        }

        Component text = Component.translatable("vampirism_the_masquerade.exposure", exp);
        int tw = mc.font.width(text);
        int limit = DRAW_W - (TEXT_PADDING * 2);
        float s = 1.0f;

        if (tw > limit) {
            s = (float) limit / tw;
            if (s < MIN_SCALE) s = MIN_SCALE;
        }

        var pose = gui.pose();
        pose.pushPose();
        pose.scale(s, s, 1.0f);

        float tx = (x + DRAW_W / 2.0f) / s - tw / 2.0f;
        float ty = (y + DRAW_H / 2.0f) / s - mc.font.lineHeight / 2.0f;

        gui.drawString(mc.font, text, (int)tx, (int)ty, color, true);

        pose.popPose();
        RenderSystem.disableBlend();
    }
}