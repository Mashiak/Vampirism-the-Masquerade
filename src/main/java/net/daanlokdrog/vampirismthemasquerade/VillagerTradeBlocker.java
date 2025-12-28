package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MerchantMenu;

import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

@EventBusSubscriber
public class VillagerTradeBlocker {

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;

        if (!(event.getContainer() instanceof MerchantMenu)) return;

        if (!Helper.isVampire(sp)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            sp.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        if (vars.exposure > 80 || vars.huntValue > 0.0) {
            sp.closeContainer();

            sp.sendSystemMessage(Component.translatable("vampirism_the_masquerade_villager_refuse"));
        }
    }
}

