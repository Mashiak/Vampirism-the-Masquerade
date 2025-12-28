package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

@EventBusSubscriber(value = Dist.CLIENT)
public class MaskTooltipEditor {

    private static final TagKey<Item> MASK_BY_ITEMTAGS =
        ItemTags.create(ResourceLocation.parse("vampirism_the_masquerade:mask"));

    private static final TagKey<Item> MASK_BY_REGISTRY =
        TagKey.create(Registries.ITEM, ResourceLocation.parse("vampirism_the_masquerade:mask"));

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        boolean isMask = event.getItemStack().getItem().builtInRegistryHolder().is(MASK_BY_ITEMTAGS)
                      || event.getItemStack().getItem().builtInRegistryHolder().is(MASK_BY_REGISTRY);

        if (isMask) {
            Component tooltip = Component.translatable("tooltip.vampirism_the_masquerade.masquerade_mask")
                                         .withStyle(ChatFormatting.DARK_PURPLE);
            event.getToolTip().add(tooltip);
        }
    }
}
