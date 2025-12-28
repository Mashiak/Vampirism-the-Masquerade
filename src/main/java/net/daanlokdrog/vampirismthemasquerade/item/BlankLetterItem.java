package net.daanlokdrog.vampirismthemasquerade.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import java.util.List;

import net.daanlokdrog.vampirismthemasquerade.HunterIntelTracker; 

public class BlankLetterItem extends Item {
    public BlankLetterItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ChunkPos chunkPos = player.chunkPosition();
            HunterIntelTracker.resetIntelForChunk(level, chunkPos);
            stack.shrink(1);
            player.sendSystemMessage(
                Component.translatable("vtm.blank_letter.intel_reset")
                    .withStyle(ChatFormatting.GREEN)
            );
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("vtm.blank_letter.tooltip").withStyle(ChatFormatting.GRAY));
    }
}