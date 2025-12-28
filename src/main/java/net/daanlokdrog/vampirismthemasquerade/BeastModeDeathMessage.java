package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class BeastModeDeathMessage {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;

        if (event.getEntity() instanceof Player killed && event.getSource().getEntity() instanceof Player killer) {
            var vars = killer.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
            
            if (vars != null && vars.beast) {
                Component message = Component.translatable(
                        "death.attack.vampirism_the_masquerade.beast_kill",
                        killed.getDisplayName(),
                        killer.getDisplayName()
                ).withStyle(ChatFormatting.WHITE);

                MinecraftServer server = killed.getServer();
                if (server != null) {
                    server.getPlayerList().broadcastSystemMessage(message, false);
                }
            }
        }
    }
}