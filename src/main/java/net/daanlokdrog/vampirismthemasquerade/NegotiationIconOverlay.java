package net.daanlokdrog.vampirismthemasquerade.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.util.VampireVillagerUtil;

@EventBusSubscriber(value = Dist.CLIENT)
public class NegotiationIconOverlay {

    private static final ResourceLocation NEGOTIATION_ICON =
        ResourceLocation.parse("vampirism_the_masquerade:textures/gui/negotiation.png");

    private static final TagKey<Item> MASQUERADE_MASK =
        ItemTags.create(ResourceLocation.parse("vampirism_the_masquerade:mask"));

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!Helper.isVampire(mc.player)) return;

        Entity target = mc.crosshairPickEntity;
        if (!VampireVillagerUtil.isVampireVillager(target)) return;

        Player viewer = mc.player;

        ItemStack head = viewer.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasMask = head.getItem().builtInRegistryHolder().is(MASQUERADE_MASK);
        VampirePlayer vPlayer = VampirePlayer.get(viewer);
        boolean disguised = vPlayer != null && vPlayer.isDisguised();
        boolean hasMasqueradeProtection = hasMask || disguised;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int iconSize = 16;
        int x = (screenWidth / 2) - (iconSize / 2) - 1;
        int y = (screenHeight / 2) + 10;

        RenderSystem.enableBlend();

        float alpha = hasMasqueradeProtection ? 0.5F : 1.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        guiGraphics.blit(NEGOTIATION_ICON, x, y, 0, 0, iconSize, iconSize, iconSize, iconSize);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
