package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class HandHeldSubmissionHandler {

    private static final Item HUMAN_HEART_ITEM = ModItems.HUMAN_HEART.get();
    private static final Item GOLD_INGOT_ITEM = Items.GOLD_INGOT;
    public static final double HUMAN_HEART_GOODWILL = 0.1; 
    public static final double PURE_BLOOD_GOODWILL = 2.0;

    public enum SubmissionType {
        HUMAN_HEART,
        PURE_BLOOD,
        GOLD_INGOT,
        NONE 
    }

    public static Optional<SubmissionInfo> checkPlayerHands(Player player) {
        Optional<SubmissionInfo> mainHandInfo = checkItem(player.getMainHandItem());
        if (mainHandInfo.isPresent()) {
            return mainHandInfo;
        }

        return checkItem(player.getOffhandItem());
    }

    private static Optional<SubmissionInfo> checkItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        Item item = stack.getItem();

        if (item == HUMAN_HEART_ITEM) {
            return Optional.of(new SubmissionInfo(SubmissionType.HUMAN_HEART, HUMAN_HEART_GOODWILL, stack.copy()));
        }

        if (item == GOLD_INGOT_ITEM) {
            return Optional.of(new SubmissionInfo(SubmissionType.GOLD_INGOT, HUMAN_HEART_GOODWILL, stack.copy()));
        }

        if (item instanceof PureBloodItem) {
            return Optional.of(new SubmissionInfo(SubmissionType.PURE_BLOOD, PURE_BLOOD_GOODWILL, stack.copy()));
        }

        return Optional.empty();
    }

    public record SubmissionInfo(SubmissionType type, double goodwillAmount, ItemStack stack) {
    }
}