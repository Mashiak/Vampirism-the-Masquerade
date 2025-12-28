package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponsmithDialogueProvider implements IDialogueProvider {

    public static final String START_PAGE = "WEAPONSMITH_START";
    private static final String UPGRADE_CONFIRM_PAGE = "WEAPONSMITH_UPGRADE_CONFIRM";
    private static final String UPGRADE_SUCCESS_PAGE = "WEAPONSMITH_UPGRADE_SUCCESS";
    private static final String NO_DOMAIN_PAGE = "WEAPONSMITH_NO_DOMAIN";
    private static final String NOT_LORD_PAGE = "WEAPONSMITH_NOT_LORD";
    private static final String INSUFFICIENT_DONATION_PAGE = "WEAPONSMITH_INSUFFICIENT_DONATION";

    private static final double DONATION_COST = 10.0;
    private static final int ATTACK_VALUE_REWARD = 1;

    private final Map<String, DialoguePage> pages;

    public WeaponsmithDialogueProvider() {
        this.pages = createDialoguePages();
    }

    private Map<String, DialoguePage> createDialoguePages() {
        Map<String, DialoguePage> map = new HashMap<>();

        map.put(START_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.start_text"),
                List.of(
                        new DialogueOption(
                                Component.translatable("vtm.weaponsmith.dialogue.option.upgrade_static",
                                        Component.literal(String.valueOf(DONATION_COST)),
                                        Component.literal(String.valueOf(ATTACK_VALUE_REWARD))
                                ),
                                1,
                                UPGRADE_CONFIRM_PAGE
                        ),
                        new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.exit"), 2, "EXIT")
                )
        ));

        map.put(UPGRADE_CONFIRM_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.confirm_upgrade"),
                List.of(
                        new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.yes_upgrade"), 1, UPGRADE_SUCCESS_PAGE),
                        new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.no_exit"), 2, START_PAGE)
                )
        ));

        map.put(UPGRADE_SUCCESS_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.upgrade_success", Component.literal(String.valueOf(ATTACK_VALUE_REWARD))),
                List.of(new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.continue"), 0, START_PAGE))
        ));

        map.put(NOT_LORD_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.not_lord"),
                List.of(new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.exit"), 0, "EXIT"))
        ));

        map.put(NO_DOMAIN_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.no_domain"),
                List.of(new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.exit"), 0, "EXIT"))
        ));

        map.put(INSUFFICIENT_DONATION_PAGE, new DialoguePage(
                Component.translatable("vtm.weaponsmith.dialogue.insufficient_donation", Component.literal(String.valueOf(DONATION_COST))),
                List.of(new DialogueOption(Component.translatable("vtm.weaponsmith.dialogue.option.exit"), 0, "EXIT"))
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

        if (player.level().isClientSide) {
            if (pages.containsKey(currentPageId)) {
                for (DialogueOption option : pages.get(currentPageId).options) {
                    if (option.packetOptionId == optionIndex) {
                        return option.nextState;
                    }
                }
            }
            return START_PAGE;
        }

        ServerLevel level = (ServerLevel) player.level();
        VampirismTheMasqueradeModVariables.PlayerVariables playerVars =
                player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (currentPageId.equals(UPGRADE_CONFIRM_PAGE) && optionIndex == 1) {
            BlockPos playerPos = player.blockPosition();
            MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
            ChunkPos playerChunk = new ChunkPos(playerPos);
            ChunkPos centerKey = villageData.getCenterKeyForChunk(playerChunk);

            if (centerKey == null) {
                return NO_DOMAIN_PAGE;
            }

            MasqueradeDomainData domainData = MasqueradeDomainData.get(level);
            String lordUUID = domainData.getLordUUID(centerKey);
            String playerUUID = player.getUUID().toString();

            if (!domainData.isDomainClaimed(centerKey) || !playerUUID.equals(lordUUID)) {
                return NOT_LORD_PAGE;
            }

            if (playerVars.donation < DONATION_COST) {
                return INSUFFICIENT_DONATION_PAGE;
            }

            playerVars.donation -= DONATION_COST;
            playerVars.markSyncDirty();

            int currentAttack = domainData.getGuardWeaponValue(centerKey);
            int newAttackValue = currentAttack + ATTACK_VALUE_REWARD;

            domainData.setGuardWeaponValue(centerKey, newAttackValue);
            domainData.setDirty();

            player.sendSystemMessage(
                    Component.translatable("vtm.weaponsmith.chat.upgrade_status",
                            Component.literal(String.valueOf(newAttackValue))
                    ).withStyle(ChatFormatting.GREEN)
            );

            return UPGRADE_SUCCESS_PAGE;
        }

        if (pages.containsKey(currentPageId)) {
            for (DialogueOption option : pages.get(currentPageId).options) {
                if (option.packetOptionId == optionIndex) {
                    return option.nextState;
                }
            }
        }

        return START_PAGE;
    }
}

