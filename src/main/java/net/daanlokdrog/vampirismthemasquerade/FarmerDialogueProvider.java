package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel; 
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos; 
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionInfo;
import net.daanlokdrog.vampirismthemasquerade.HandHeldSubmissionHandler.SubmissionType;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FarmerDialogueProvider implements IDialogueProvider {

    private static final String START_PAGE = "FARMER_START";
    private static final String SUBMIT_CHECK_PAGE = "FARMER_SUBMIT_CHECK";
    private static final String HEART_RECEIVED_PAGE = "FARMER_HEART_RECEIVED";
    private static final String BLOOD_RECEIVED_PAGE = "FARMER_BLOOD_RECEIVED";
    private static final String NOTHING_TO_GIVE_PAGE = "FARMER_NOTHING_TO_GIVE";
    private static final String LORD_LEVEL_INSUFFICIENT_PAGE = "FARMER_LORD_LEVEL_INSUFFICIENT";
    private static final double HABITABILITY_THRESHOLD = 50.0;
    private static final int LORD_LEVEL_THRESHOLD = 1;

    private final Map<String, DialoguePage> pages;

    public FarmerDialogueProvider() {
        this.pages = createDialoguePages();
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        double heartGoodwill = HandHeldSubmissionHandler.HUMAN_HEART_GOODWILL;
        double bloodGoodwill = HandHeldSubmissionHandler.PURE_BLOOD_GOODWILL;

        map.put(START_PAGE, new DialoguePage(
                Component.translatable("vtm.farmer.dialogue.start_text"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.offer_gift"), 1, SUBMIT_CHECK_PAGE),
                        new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.exit"), 2, "EXIT")
                )
        ));

        map.put(SUBMIT_CHECK_PAGE, new DialoguePage(
                Component.literal("..."),
                List.of(new DialogueOption(Component.literal("..."), 0, START_PAGE))
        ));

        map.put(HEART_RECEIVED_PAGE, new DialoguePage(
                Component.translatable("vtm.farmer.dialogue.heart_received", Component.literal(String.valueOf(heartGoodwill))),
                List.of(new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(BLOOD_RECEIVED_PAGE, new DialoguePage(
                Component.translatable("vtm.farmer.dialogue.blood_received", Component.literal(String.valueOf(bloodGoodwill))),
                List.of(new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(NOTHING_TO_GIVE_PAGE, new DialoguePage(
                Component.translatable("vtm.farmer.dialogue.nothing_to_give"),
                List.of(new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.exit"), 0, "EXIT"))
        ));
        
        map.put(LORD_LEVEL_INSUFFICIENT_PAGE, new DialoguePage(
                Component.translatable("vtm.farmer.dialogue.lord_level_insufficient"),
                List.of(new DialogueOption(Component.translatable("vtm.farmer.dialogue.option.exit"), 0, "EXIT"))
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
                ServerLevel level = (ServerLevel) player.level();

                ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
                ChunkPos centerKey = villageData.getCenterKeyForChunk(playerChunk);

                if (centerKey == null) {
                    player.sendSystemMessage(Component.translatable("vtm.farmer.dialogue.warning.domain_lost"));
                    return "EXIT";
                }

                MasqueradeDomainData domainData = MasqueradeDomainData.get(level);
                double currentHabitability = domainData.getHabitability(centerKey);
                int lordLevel = domainData.getLordLevel(centerKey);

                if (currentHabitability >= HABITABILITY_THRESHOLD && lordLevel < LORD_LEVEL_THRESHOLD) {
                    return LORD_LEVEL_INSUFFICIENT_PAGE;
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
                    double rewardGoodwill = info.goodwillAmount();

                    if (info.type() == SubmissionType.HUMAN_HEART && currentHabitability >= HABITABILITY_THRESHOLD) {
                        rewardGoodwill /= 2.0;
                    }

                    double totalGoodwill = rewardGoodwill * totalConsumedCount;

                    domainData.addHabitability(centerKey, totalGoodwill);

                    return info.type() == SubmissionType.HUMAN_HEART ? HEART_RECEIVED_PAGE : BLOOD_RECEIVED_PAGE;
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