package net.daanlokdrog.vampirismthemasquerade.client;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.daanlokdrog.vampirismthemasquerade.data.ThrallData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class ThrallIconOverlay {

    private static final ResourceLocation THRALL_ICON =
        ResourceLocation.parse("vampirism_the_masquerade:textures/gui/thrall_icon.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Entity target = mc.crosshairPickEntity;
        if (!(target instanceof Villager villager)) return;

        if (!ThrallData.isThrall(villager)) return;

        float bloodLevelRelative = Optional.ofNullable(ExtendedCreature.getSafe(villager))
                .flatMap(opt -> opt.map(IBiteableEntity::getBloodLevelRelative))
                .map(v -> Mth.clamp(v, 0.0F, 1.0F))
                .orElseGet(() -> {
                    if (villager instanceof IBiteableEntity biteable) {
                        return Mth.clamp(biteable.getBloodLevelRelative(), 0.0F, 1.0F);
                    }
                    return 1.0F;
                });

        float alpha = Math.max(0.2F, bloodLevelRelative);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int iconSize = 32;
        int x = (screenWidth / 2) - (iconSize / 2);
        int y = (screenHeight / 2) - (iconSize / 2);

        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(THRALL_ICON, x, y, 0, 0, iconSize, iconSize, iconSize, iconSize);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
