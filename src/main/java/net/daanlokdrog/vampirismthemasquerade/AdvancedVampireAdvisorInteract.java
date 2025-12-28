package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.ChunkPos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedVampireAdvisorInteract {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedVampireAdvisorInteract.class);

    private static final String LORD_UUID = "VTM_LordUUID";
    private static final String ADVISOR_TAG = "IsAdvisor";
    private static final String CK_X = "VTM_CenterKeyX";
    private static final String CK_Z = "VTM_CenterKeyZ";

    private static final String MSG_NOT_YOUR_ADVISOR = "vtm.advisor.error.not_your_advisor";
    private static final String MSG_NO_DIALOGUE = "vtm.advisor.error.no_dialogue";

    private static void ensureAdvisorUUIDSynced(ServerLevel level, AdvancedVampireEntity advisor) {
        CompoundTag tag = advisor.getPersistentData();
        if (!tag.getBoolean(ADVISOR_TAG)) return;

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);

        ChunkPos centerKey = villageData.getCenterKeyForChunk(new ChunkPos(advisor.blockPosition()));

        if (centerKey == null && tag.contains(CK_X) && tag.contains(CK_Z)) {
            centerKey = new ChunkPos(tag.getInt(CK_X), tag.getInt(CK_Z));
        }

        if (centerKey == null) return;

        String domainLordUUID = domainData.getLordUUID(centerKey);
        if (domainLordUUID == null || domainLordUUID.isEmpty()) return;

        String stored = tag.getString(LORD_UUID);
        if (!domainLordUUID.equals(stored)) {
            tag.putString(LORD_UUID, domainLordUUID);
        }
    }

    public static InteractionResult openNegotiationMenu(ServerPlayer serverPlayer, AdvancedVampireEntity advisor) {

        if (advisor.level() instanceof ServerLevel level) {
            ensureAdvisorUUIDSynced(level, advisor);
        }

        CompoundTag entityTag = advisor.getPersistentData();
        String storedLordUUID = entityTag.getString(LORD_UUID);
        String playerUUID = serverPlayer.getUUID().toString();

        if (storedLordUUID.isEmpty() || !playerUUID.equals(storedLordUUID)) {
            Component text = Component.translatable(MSG_NOT_YOUR_ADVISOR).withStyle(ChatFormatting.RED);
            serverPlayer.sendSystemMessage(
                advisor.getDisplayName().copy().withStyle(ChatFormatting.DARK_PURPLE)
                    .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                    .append(text)
            );
            return InteractionResult.FAIL;
        }

        IDialogueProvider provider = DialogueProviderRegistry.getProvider(advisor);

        if (provider == null) {
            serverPlayer.sendSystemMessage(Component.translatable(MSG_NO_DIALOGUE).withStyle(ChatFormatting.RED));
            LOGGER.warn("Attempted to open menu for Advisor, but no dialogue provider was registered for type {}.",
                advisor.getType().builtInRegistryHolder().key().location());
            return InteractionResult.FAIL;
        }

        String startPageId = provider.getStartPageId();

        LOGGER.info("Lord {} opening Negotiation Menu for Advisor {} with Start ID: {}.",
            serverPlayer.getName().getString(), advisor.getName().getString(), startPageId);

        MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("gui.vampirism_the_masquerade.negotiation_menu_advisor", advisor.getDisplayName());
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return new NegotiationMenu(id, inventory, advisor.getId(), startPageId);
            }
        };

        serverPlayer.openMenu(containerProvider, buf -> {
            buf.writeInt(advisor.getId());
            buf.writeUtf(startPageId);
        });

        return InteractionResult.CONSUME;
    }
}
