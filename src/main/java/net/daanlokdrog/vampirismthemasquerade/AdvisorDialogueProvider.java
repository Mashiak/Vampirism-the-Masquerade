package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class AdvisorDialogueProvider implements IDialogueProvider {

    private final Map<String, DialoguePage> pages;

    private static final String START_PAGE_ID = "ADVISOR_START";
    private static final String EDICT_MAIN_ID = "EDICTS_MAIN";
    private static final String ACTION_TOGGLE_MONSTER_PROTECT = "TOGGLE_MONSTER_PROTECT";
    private static final String ACTION_TOGGLE_FORBID_FEEDING = "TOGGLE_FORBID_FEEDING";
    private static final String CENTER_KEY_X_TAG = "VTM_CenterKeyX";
    private static final String CENTER_KEY_Z_TAG = "VTM_CenterKeyZ";

    public AdvisorDialogueProvider() {
        this.pages = new HashMap<>();
        this.pages.putAll(createDialoguePages());
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        map.put(START_PAGE_ID, new DialoguePage(
            Component.translatable("vtm.advisor.dialogue.start").withStyle(ChatFormatting.DARK_PURPLE),
            List.of(
                new DialogueOption(Component.translatable("vtm.advisor.option.edicts"), 1, EDICT_MAIN_ID),
                new DialogueOption(Component.translatable("vtm.advisor.option.exit"), 2, "EXIT")
            )
        ));

        map.put(EDICT_MAIN_ID, new DialoguePage(
            Component.translatable("vtm.advisor.dialogue.edicts_menu").withStyle(ChatFormatting.DARK_PURPLE),
            List.of(
                new DialogueOption(Component.translatable("vtm.advisor.option.monster_protect"), 1, ACTION_TOGGLE_MONSTER_PROTECT),
                new DialogueOption(Component.translatable("vtm.advisor.option.forbid_feeding"), 2, ACTION_TOGGLE_FORBID_FEEDING),
                new DialogueOption(Component.translatable("vtm.advisor.option.back"), 3, START_PAGE_ID)
            )
        ));

        return map;
    }

    @Override
    public Map<String, DialoguePage> getDialoguePages() {
        return pages;
    }

    @Override
    public String getStartPageId() {
        return START_PAGE_ID;
    }

    @Override
    public String getNextPageId(Player player, Entity entity, String currentPageId, int optionIndex) {
        if (player.level().isClientSide) {
            if (pages.containsKey(currentPageId)) {
                for (DialogueOption option : pages.get(currentPageId).options) {
                    if (option.packetOptionId == optionIndex) {
                        return option.nextState;
                    }
                }
            }
            return START_PAGE_ID;
        }

        ServerLevel level = (ServerLevel) player.level();
        DialoguePage sourcePage = pages.get(currentPageId);

        if (sourcePage == null) {
            return START_PAGE_ID;
        }

        String nextStateId = null;
        for (DialogueOption option : sourcePage.options) {
            if (option.packetOptionId == optionIndex) {
                nextStateId = option.nextState;
                break;
            }
        }

        if (nextStateId == null) {
            return START_PAGE_ID;
        }

        if (currentPageId.equals(EDICT_MAIN_ID)
            && (nextStateId.equals(ACTION_TOGGLE_MONSTER_PROTECT) || nextStateId.equals(ACTION_TOGGLE_FORBID_FEEDING))) {
            handleEdictToggleAndFeedback(player, level, (AdvancedVampireEntity) entity, nextStateId);
            return EDICT_MAIN_ID;
        }

        return nextStateId;
    }

    private void handleEdictToggleAndFeedback(Player player, ServerLevel level, AdvancedVampireEntity advisor, String actionId) {
        Optional<ChunkPos> centerKeyOpt = getDomainCenterKey(advisor);

        if (centerKeyOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("vtm.advisor.feedback.no_domain").withStyle(ChatFormatting.RED));
            return;
        }

        ChunkPos centerKey = centerKeyOpt.get();
        DomainEdictStorage edictStorage = DomainEdictStorage.get(level);

        boolean newState;
        String edictNameKey;

        switch (actionId) {
            case ACTION_TOGGLE_MONSTER_PROTECT:
                boolean currentM = edictStorage.isMonsterProtectLordEnabled(centerKey);
                newState = !currentM;
                edictStorage.setMonsterProtectLordEnabled(centerKey, newState);
                edictNameKey = "vtm.advisor.option.monster_protect";
                break;

            case ACTION_TOGGLE_FORBID_FEEDING:
                boolean currentF = edictStorage.isForbidOutsiderFeedingEnabled(centerKey);
                newState = !currentF;
                edictStorage.setForbidOutsiderFeedingEnabled(centerKey, newState);
                edictNameKey = "vtm.advisor.option.forbid_feeding";
                break;

            default:
                return;
        }

        MutableComponent edictName = Component.translatable(edictNameKey);
        MutableComponent statusText = newState
            ? Component.translatable("vtm.advisor.status.activated").withStyle(ChatFormatting.GREEN)
            : Component.translatable("vtm.advisor.status.disabled").withStyle(ChatFormatting.RED);

        MutableComponent feedback = Component.translatable(
            "vtm.advisor.feedback.status_update",
            edictName,
            statusText
        );

        player.sendSystemMessage(feedback);
    }

    private Optional<ChunkPos> getDomainCenterKey(AdvancedVampireEntity advisor) {
        CompoundTag entityTag = advisor.getPersistentData();

        if (entityTag.contains(CENTER_KEY_X_TAG, Tag.TAG_INT) && entityTag.contains(CENTER_KEY_Z_TAG, Tag.TAG_INT)) {
            int x = entityTag.getInt(CENTER_KEY_X_TAG);
            int z = entityTag.getInt(CENTER_KEY_Z_TAG);
            return Optional.of(new ChunkPos(x, z));
        }

        return Optional.empty();
    }
}
