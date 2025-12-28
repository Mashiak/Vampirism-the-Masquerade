package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes; 
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.NegotiationMenu; 
import net.daanlokdrog.vampirismthemasquerade.MasqueradeHelper; 
import net.daanlokdrog.vampirismthemasquerade.util.VampireVillagerUtil;

@EventBusSubscriber
public class VampirismVillagerInteract {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!VampireVillagerUtil.isVampireVillager(event.getTarget()) ||
            !(event.getEntity() instanceof ServerPlayer serverPlayer) ||
            event.getHand() != event.getEntity().getUsedItemHand()) {
            return;
        }

        Player player = event.getEntity();

        if (!Helper.isVampire(player)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        boolean isUnderMasqueradeProtection = MasqueradeHelper.isUnderMasqueradeProtection(player);

        if (isUnderMasqueradeProtection) {
            player.sendSystemMessage(event.getTarget().getName().copy()
                .append(Component.translatable("vampirism_the_masquerade.mask_talk")));
            return;

        } else {
            event.setCanceled(true);

            double exposure = (vars != null) ? vars.exposure : 0.0;
            int exposureInt = (int) Math.round(exposure);

            Component message;
            boolean shouldOpenNegotiation = false;

            if (exposureInt >= 80) {
                message = event.getTarget().getName().copy()
                    .append(Component.translatable("vampirism_the_masquerade.v_villager_warn3"));
            } else if (exposureInt > 40) {
                message = event.getTarget().getName().copy()
                    .append(Component.translatable("vampirism_the_masquerade.v_villager_warn2"));
            } else if (exposureInt > 20) {
                message = event.getTarget().getName().copy()
                    .append(Component.translatable("vampirism_the_masquerade.v_villager_warn1"));
            } else {
                shouldOpenNegotiation = true;

                VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
                int vampireLevel = atts.vampireLevel;
                int lordLevel = atts.lordLevel;

                String dialogueKey;

                if (lordLevel > 0) {
                    dialogueKey = "vampirism_the_masquerade.v_villager_talk3";
                } else if (vampireLevel >= 10) {
                    dialogueKey = "vampirism_the_masquerade.v_villager_talk2";
                } else {
                    dialogueKey = "vampirism_the_masquerade.v_villager_talk1";
                }

                message = event.getTarget().getName().copy()
                    .append(Component.translatable(dialogueKey));
            }

            player.sendSystemMessage(message);

            if (shouldOpenNegotiation) {
                String startPageId = DialogueProviderRegistry.getStartPageId(event.getTarget());

                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("gui.vampirism_the_masquerade.negotiation_menu");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new NegotiationMenu(id, inventory, event.getTarget().getId());
                    }
                };

                serverPlayer.openMenu(containerProvider, buf -> {
                    buf.writeInt(event.getTarget().getId());
                    buf.writeUtf(startPageId);
                });

                VampirismTheMasqueradeMod.LOGGER.info(
                    "Conditions met for Negotiation Menu. (Exposure: {}). Opening with Page: {}.",
                    exposureInt, startPageId);
            }
        }
    }
}
