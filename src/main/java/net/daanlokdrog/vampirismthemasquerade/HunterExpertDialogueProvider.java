package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionInfo;
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionType;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModItems; 
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HunterExpertDialogueProvider implements IDialogueProvider {

    private static final String START_PAGE = "HUNTER_START";
    private static final String TRADE_PAGE = "HUNTER_TRADE_OFFER";
    private static final String TRADE_SUCCESS_PAGE = "HUNTER_TRADE_SUCCESS";
    private static final String NOT_ENOUGH_GOLD_PAGE = "HUNTER_NOT_ENOUGH_GOLD";
    private static final String INVENTORY_FULL_PAGE = "HUNTER_INVENTORY_FULL";

    private static final int GOLD_COST = 24;

    private final Map<String, DialoguePage> pages;

    public HunterExpertDialogueProvider() {
        this.pages = createDialoguePages();
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        map.put(START_PAGE, new DialoguePage(
                Component.translatable("vtm.hunter_expert.dialogue.start_text"),
                List.of(
                        new DialogueOption(
                            Component.translatable("vtm.hunter_expert.dialogue.option.trade", 
                                Component.literal(String.valueOf(GOLD_COST))),
                            1, 
                            TRADE_PAGE
                        ),
                        new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.exit"), 2, "EXIT")
                )
        ));

        map.put(TRADE_PAGE, new DialoguePage(
                Component.translatable("vtm.hunter_expert.dialogue.trade_offer"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.confirm"), 1, TRADE_SUCCESS_PAGE),
                        new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.cancel"), 2, START_PAGE)
                )
        ));

        map.put(TRADE_SUCCESS_PAGE, new DialoguePage(
                Component.translatable("vtm.hunter_expert.dialogue.trade_success"),
                List.of(new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(NOT_ENOUGH_GOLD_PAGE, new DialoguePage(
                Component.translatable("vtm.hunter_expert.dialogue.not_enough_gold", 
                    Component.literal(String.valueOf(GOLD_COST))),
                List.of(new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.exit"), 0, "EXIT"))
        ));

        map.put(INVENTORY_FULL_PAGE, new DialoguePage(
                Component.translatable("vtm.hunter_expert.dialogue.inventory_full"),
                List.of(new DialogueOption(Component.translatable("vtm.hunter_expert.dialogue.option.exit"), 0, "EXIT"))
        ));

        return map;
    }

    @Override
    public Map<String, DialoguePage> getDialoguePages() {
        return pages;
    }

    @Override
    public String getStartPageId() {
        return START_PAGE;
    }

@Override
public String getNextPageId(Player player, Entity entity, String currentPageId, int optionIndex) {

    if (currentPageId.equals(TRADE_PAGE) && optionIndex == 1) {
        if (!player.level().isClientSide) {
            int goldCount = player.getInventory().countItem(Items.GOLD_INGOT);

            if (goldCount < GOLD_COST) {
                return NOT_ENOUGH_GOLD_PAGE;
            }

            ItemStack letterStack = new ItemStack(VampirismTheMasqueradeModItems.BLANK_LETTER.get());

            if (player.getInventory().getFreeSlot() == -1) {
                return INVENTORY_FULL_PAGE;
            }

            int remainingToConsume = GOLD_COST;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.is(Items.GOLD_INGOT)) {
                    int canConsume = Math.min(remainingToConsume, stack.getCount());
                    stack.shrink(canConsume);
                    remainingToConsume -= canConsume;
                    if (remainingToConsume <= 0) {
                        break;
                    }
                }
            }

            if (remainingToConsume > 0) {
                return NOT_ENOUGH_GOLD_PAGE;
            }

            player.getInventory().add(letterStack);

            return TRADE_SUCCESS_PAGE;
        }
    }

    return DialogueUtils.safeGetNextPageId(pages, currentPageId, optionIndex, getStartPageId());
}

}