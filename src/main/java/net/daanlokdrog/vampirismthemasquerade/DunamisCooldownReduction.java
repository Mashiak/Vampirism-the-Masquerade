package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.event.ActionEvent;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import net.minecraft.world.entity.LivingEntity;

@EventBusSubscriber
public class DunamisCooldownReduction {

    private static int calculateReducedCooldown(LivingEntity livingEntity, int originalCooldown) {
        if (originalCooldown <= 0) {
            return 0;
        }

        double dunamisValue = 0.0;
        try {
            VampirismTheMasqueradeModVariables.PlayerVariables vars =
                livingEntity.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
            dunamisValue = vars.dunamis;
        } catch (Exception e) {
            return originalCooldown;
        }

        if (dunamisValue <= 0.0) {
            return originalCooldown;
        }

        double rf = dunamisValue * 0.005;
        double er = Math.min(rf, 0.90);
        double cooldownMulti = 1.0 - er;
        int reducedCooldown = (int) (originalCooldown * cooldownMulti);

        return Math.max(1, reducedCooldown);
    }

    @SubscribeEvent
    public static void onActionActivated(ActionEvent.ActionActivatedEvent event) {
        IFactionPlayer<?> factionPlayer = event.getFactionPlayer();
        LivingEntity livingEntity = factionPlayer.asEntity();
        int originalCooldown = event.getCooldown();
        int reducedCooldown = calculateReducedCooldown(livingEntity, originalCooldown);
        event.setCooldown(reducedCooldown);
        IActionHandler<?> handler = factionPlayer.getActionHandler();
        @SuppressWarnings({"rawtypes", "unchecked"})
        IAction rawAction = (IAction) event.getAction();
        handler.resetTimer(rawAction);
    }

    @SubscribeEvent
    public static void onActionDeactivated(ActionEvent.ActionDeactivatedEvent event) {
        if (!(event.getAction() instanceof ILastingAction)) {
            return;
        }

        IFactionPlayer<?> factionPlayer = event.getFactionPlayer();
        LivingEntity livingEntity = factionPlayer.asEntity();
        int originalCooldown = event.getCooldown();
        int reducedCooldown = calculateReducedCooldown(livingEntity, originalCooldown);
        event.setCooldown(reducedCooldown);
    }
}
