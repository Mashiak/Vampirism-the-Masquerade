package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.core.ModDamageTypes;

@EventBusSubscriber
public class VampireDamageWitness {

    @SubscribeEvent
    public static void onVampireDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!Helper.isVampire(player)) return;

        DamageSource source = event.getSource();

        if (source.is(ModDamageTypes.SUN_DAMAGE) || source.is(ModDamageTypes.HOLY_WATER)) {
            VillagerWitnessEvent.handleWitness(player);
        }
    }
}
