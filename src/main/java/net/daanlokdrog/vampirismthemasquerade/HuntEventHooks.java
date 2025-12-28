package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

@EventBusSubscriber
public class HuntEventHooks {
	
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            restoreBossBar(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            restoreBossBar(sp);
        }
    }

    private static void restoreBossBar(ServerPlayer sp) {
        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            sp.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars != null && vars.huntValue > 0) {
            ServerBossEvent bossBar = HuntEventManager.getActiveBar(sp.getUUID());
            
            if (bossBar == null) {
                bossBar = new ServerBossEvent(
                    Component.translatable("vampirism_the_masquerade_hunt"),
                    BossEvent.BossBarColor.WHITE,
                    BossEvent.BossBarOverlay.PROGRESS
                );
                HuntEventManager.putActiveBar(sp.getUUID(), bossBar);
            }

            if (!bossBar.getPlayers().contains(sp)) {
                bossBar.addPlayer(sp);
            }

            bossBar.setProgress((float)(vars.huntValue / 100.0));

            if (vars.huntBegining) {
                bossBar.setName(Component.translatable("vampirism_the_masquerade_huntBegin", sp.getName()));
                bossBar.setColor(BossEvent.BossBarColor.BLUE);
            } else {
                bossBar.setName(Component.translatable("vampirism_the_masquerade_hunt"));
                bossBar.setColor(BossEvent.BossBarColor.WHITE);
            }
        }
    }
}