package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionInfo;
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionType;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.world.entity.Entity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.daanlokdrog.vampirismthemasquerade.DialogueUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExpertDialogueProvider implements IDialogueProvider {

    private static final String START_PAGE = "START";
    private static final String SUBMIT_CHECK_PAGE = "EXPERT_SUBMIT_CHECK";
    private static final String HEART_RECEIVED_PAGE = "EXPERT_HEART_RECEIVED";
    private static final String BLOOD_RECEIVED_PAGE = "EXPERT_BLOOD_RECEIVED";
    private static final String GOLD_RECEIVED_PAGE = "EXPERT_GOLD_RECEIVED";
    private static final String NOTHING_TO_GIVE_PAGE = "EXPERT_NOTHING_TO_GIVE";
    private static final double HEART_DONATION = 0.1;
    private static final double GOLD_DONATION = 0.1;
    private static final double BLOOD_DONATION = 0.5;

    private final Map<String, DialoguePage> pages;

    public ExpertDialogueProvider() {
        this.pages = createDialoguePages();
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        map.put(START_PAGE, new DialoguePage(
                Component.translatable("vtm.expert.dialogue.start_text"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.expert.dialogue.option.offer_gift"), 1, SUBMIT_CHECK_PAGE),
                        new DialogueOption(Component.translatable("vtm.expert.dialogue.option.exit"), 2, "EXIT")
                )
        ));

        map.put(SUBMIT_CHECK_PAGE, new DialoguePage(
                Component.literal("... (The Expert evaluates your offering)"),
                List.of(new DialogueOption(Component.literal("..."), 0, START_PAGE))
        ));

        map.put(HEART_RECEIVED_PAGE, new DialoguePage(
                Component.translatable("vtm.expert.dialogue.heart_received", Component.literal(String.valueOf(HEART_DONATION))),
                List.of(new DialogueOption(Component.translatable("vtm.expert.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(BLOOD_RECEIVED_PAGE, new DialoguePage(
                Component.translatable("vtm.expert.dialogue.blood_received", Component.literal(String.valueOf(BLOOD_DONATION))),
                List.of(new DialogueOption(Component.translatable("vtm.expert.dialogue.option.continue"), 0, START_PAGE))
        ));
        
        map.put(GOLD_RECEIVED_PAGE, new DialoguePage(
                Component.translatable("vtm.expert.dialogue.gold_received", Component.literal(String.valueOf(GOLD_DONATION))), 
                List.of(new DialogueOption(Component.translatable("vtm.expert.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(NOTHING_TO_GIVE_PAGE, new DialoguePage(
                Component.translatable("vtm.expert.dialogue.nothing_to_give"),
                List.of(new DialogueOption(Component.translatable("vtm.expert.dialogue.option.exit"), 0, "EXIT"))
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
    if (currentPageId.equals(START_PAGE) && optionIndex == 1) {
        Optional<SubmissionInfo> submission = HandHeldSubmissionHandler.checkPlayerHands(player);
        if (submission.isPresent()) {
            SubmissionInfo info = submission.get();
            if (!player.level().isClientSide) {
                VampirismTheMasqueradeModVariables.PlayerVariables playerVars =
                        player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
                double singleItemDonation;
                String nextPageId;

                switch (info.type()) {
                    case HUMAN_HEART:
                        singleItemDonation = HEART_DONATION;
                        nextPageId = HEART_RECEIVED_PAGE;
                        break;
                    case PURE_BLOOD:
                        singleItemDonation = BLOOD_DONATION;
                        nextPageId = BLOOD_RECEIVED_PAGE;
                        break;
                    case GOLD_INGOT:
                        singleItemDonation = GOLD_DONATION;
                        nextPageId = GOLD_RECEIVED_PAGE;
                        break;
                    default:
                        return NOTHING_TO_GIVE_PAGE;
                }

                int totalConsumedCount = 0;
                Item submittedItemType = info.stack().getItem();

                ItemStack mainHandStack = player.getMainHandItem();
                if (!mainHandStack.isEmpty() && mainHandStack.is(submittedItemType)) {
                    int count = mainHandStack.getCount();
                    mainHandStack.shrink(count);
                    totalConsumedCount += count;
                }

                ItemStack offHandStack = player.getOffhandItem();
                if (!offHandStack.isEmpty() && offHandStack.is(submittedItemType)) {
                    int count = offHandStack.getCount();
                    offHandStack.shrink(count);
                    totalConsumedCount += count;
                }

                if (totalConsumedCount > 0) {
                    double totalDonation = singleItemDonation * totalConsumedCount;
                    playerVars.donation += totalDonation;
                    playerVars.markSyncDirty();
                    return nextPageId;
                }
            }
            return NOTHING_TO_GIVE_PAGE;
        }
        return NOTHING_TO_GIVE_PAGE;
    }

    if (currentPageId.equals(SUBMIT_CHECK_PAGE)) {
        return START_PAGE;
    }

    return DialogueUtils.safeGetNextPageId(pages, currentPageId, optionIndex, getStartPageId());
}

}