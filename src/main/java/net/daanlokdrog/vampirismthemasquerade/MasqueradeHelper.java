package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;

public class MasqueradeHelper {

    private static final TagKey<Item> MASQUERADE_MASK =
        ItemTags.create(ResourceLocation.parse("vampirism_the_masquerade:mask"));

    public static boolean isUnderMasqueradeProtection(Player player) {
        ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        boolean hasMask = head.getItem().builtInRegistryHolder().is(MASQUERADE_MASK);
        VampirePlayer vPlayer = VampirePlayer.get(player);
        boolean isDisguisedSkillActive = vPlayer != null && vPlayer.isDisguised();
        return hasMask || isDisguisedSkillActive;
    }
}