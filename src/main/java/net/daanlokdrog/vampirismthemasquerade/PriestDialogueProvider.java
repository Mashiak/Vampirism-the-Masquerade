package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.daanlokdrog.vampirismthemasquerade.DialogueUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; 

public class PriestDialogueProvider implements IDialogueProvider {

    private static final String START_PAGE = "START";
    private static final String PRAY_CONFIRM_PAGE = "PRIEST_PRAY_CONFIRM";
    private static final String PRAYER_GRANTED_PAGE = "PRIEST_PRAYER_GRANTED";
    private static final String ALREADY_PRAYED_PAGE = "PRIEST_ALREADY_PRAYED";
    private static final String NO_COST_PAGE = "PRIEST_NO_COST"; 
    
    private static final long TICKS_PER_DAY = 24000L;
    private static final int EXPOSURE_REDUCTION = 20;
    private static final double PANIC_REDUCTION = 5.0;

    private final Map<String, DialoguePage> pages;

    public PriestDialogueProvider() {
        this.pages = createDialoguePages();
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        map.put(START_PAGE, new DialoguePage(
                Component.translatable("vtm.priest.dialogue.start_text"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.priest.dialogue.option.seek_prayer"), 1, PRAY_CONFIRM_PAGE), 
                        new DialogueOption(Component.translatable("vtm.priest.dialogue.option.exit"), 2, "EXIT")
                )
        ));

        map.put(PRAY_CONFIRM_PAGE, new DialoguePage(
                Component.translatable("vtm.priest.dialogue.confirm_prayer"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.priest.dialogue.option.yes_pray"), 1, PRAYER_GRANTED_PAGE), 
                        new DialogueOption(Component.translatable("vtm.priest.dialogue.option.no_exit"), 2, START_PAGE)
                )
        ));

        map.put(PRAYER_GRANTED_PAGE, new DialoguePage(
                Component.translatable("vtm.priest.dialogue.prayer_granted", 
                    Component.literal(String.valueOf(EXPOSURE_REDUCTION)), 
                    Component.literal(String.valueOf(PANIC_REDUCTION))
                ),
                List.of(new DialogueOption(Component.translatable("vtm.priest.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(ALREADY_PRAYED_PAGE, new DialoguePage(
                Component.translatable("vtm.priest.dialogue.already_prayed"),
                List.of(new DialogueOption(Component.translatable("vtm.priest.dialogue.option.exit"), 0, "EXIT"))
        ));

        map.put(NO_COST_PAGE, new DialoguePage(
                Component.translatable("vtm.priest.dialogue.no_cost"), 
                List.of(new DialogueOption(Component.translatable("vtm.priest.dialogue.option.exit"), 0, "EXIT"))
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
        if (!player.level().isClientSide) {
            ServerLevel level = (ServerLevel) player.level();
            long currentTime = level.getGameTime();
            VampirismTheMasqueradeModVariables.PlayerVariables playerVars =
                    player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

            if (playerVars.lastPriestPrayerTime > 0 &&
                (currentTime - playerVars.lastPriestPrayerTime) < TICKS_PER_DAY) {
                return ALREADY_PRAYED_PAGE;
            }
            return PRAY_CONFIRM_PAGE;
        }
        return PRAY_CONFIRM_PAGE;
    }

    if (currentPageId.equals(PRAY_CONFIRM_PAGE) && optionIndex == 1) {
        if (!player.level().isClientSide) {
            ServerLevel level = (ServerLevel) player.level();
            long currentTime = level.getGameTime();
            VampirismTheMasqueradeModVariables.PlayerVariables playerVars =
                    player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

            if (playerVars.lastPriestPrayerTime > 0 &&
                (currentTime - playerVars.lastPriestPrayerTime) < TICKS_PER_DAY) {
                return ALREADY_PRAYED_PAGE;
            }

            playerVars.lastPriestPrayerTime = currentTime;
            playerVars.exposure = Math.max(0, playerVars.exposure - EXPOSURE_REDUCTION);

            BlockPos playerPos = player.blockPosition();
            MasqueradeVillageData data = MasqueradeVillageData.get(level);
            ChunkPos playerChunk = new ChunkPos(playerPos);
            ChunkPos centerKey = data != null ? data.getCenterKeyForChunk(playerChunk) : null;

            if (data != null && centerKey != null) {
                data.addPanicForChunk(centerKey, -PANIC_REDUCTION);
            }

            playerVars.markSyncDirty();
            return PRAYER_GRANTED_PAGE;
        }
        return PRAYER_GRANTED_PAGE;
    }
    return DialogueUtils.safeGetNextPageId(pages, currentPageId, optionIndex, getStartPageId());
}

}