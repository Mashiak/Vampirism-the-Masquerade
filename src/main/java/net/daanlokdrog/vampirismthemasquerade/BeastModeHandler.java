package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

@EventBusSubscriber
public class BeastModeHandler {

    private static final ResourceLocation SPEED_MODIFIER_RL = ResourceLocation.parse("vtm_masquerade:beast_speed");
    private static final int HEARTBEAT_INTERVAL = 40;
    private static final double STRONG_HEARTBEAT_THRESHOLD = 36.0;

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !(player instanceof ServerPlayer)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars.beast_cooldown > 0) {
            vars.beast_cooldown--;
            if (vars.beast) vars.beast = false;
            vars.markSyncDirty();
        }

        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            boolean hasModifier = speedAttr.hasModifier(SPEED_MODIFIER_RL);

            if (vars.beast) {
                if (!hasModifier) {
                    speedAttr.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_RL, 0.6, AttributeModifier.Operation.ADD_VALUE));
                }

                if (++vars.heartbeatTickCounter >= HEARTBEAT_INTERVAL) {
                    vars.heartbeatTickCounter = 0;
                    var sound = vars.dunamis > STRONG_HEARTBEAT_THRESHOLD ? ModSoundEvents.HEART_STRONG.get() : ModSoundEvents.HEART_WEAK.get();
                    player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0f, 1.0f);
                    vars.markSyncDirty();
                }
            } else {
                if (hasModifier) speedAttr.removeModifier(SPEED_MODIFIER_RL);
                vars.heartbeatTickCounter = 0;
            }
        }

        if (!vars.beast || vars.beast_cooldown > 0) return;

        if (++tickCounter < 20) return;
        tickCounter = 0;

        if (vars.dunamis >= 4.0) {
            vars.dunamis = Math.max(0, vars.dunamis - 4.0);
            vars.markSyncDirty();
        } else {
            vars.dunamis = 0;
            vars.beast = false;
            vars.beast_cooldown = 12000;
            player.sendSystemMessage(Component.translatable("vtm.beast.sleeping"));
            vars.markSyncDirty();
        }
    }
}