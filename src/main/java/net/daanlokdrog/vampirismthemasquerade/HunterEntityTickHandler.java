package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

@EventBusSubscriber
public class HunterEntityTickHandler {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof BasicHunterEntity) &&
            !(event.getEntity() instanceof AdvancedHunterEntity)) {
            return;
        }

        if (!event.getEntity().getPersistentData().getBoolean("masquerade_spawned")) return;

        if (event.getEntity().tickCount % 40 != 0) return;

        ServerLevel level = (ServerLevel) event.getEntity().level();

        for (ServerPlayer sp : level.players()) {
            VampirismTheMasqueradeModVariables.PlayerVariables vars =
                sp.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

            if (vars != null && vars.huntActive) {
                if (event.getEntity() instanceof BasicHunterEntity hunter) {
                    hunter.setTarget(sp);
                } else if (event.getEntity() instanceof AdvancedHunterEntity hunter) {
                    hunter.setTarget(sp);
                }
                break;
            }
        }
    }
}

