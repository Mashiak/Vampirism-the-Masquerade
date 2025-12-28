package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Pillager;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class LordVampireTarget {

    @SubscribeEvent
    public static void onNeutralFactionTargetChange(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        boolean isNeutralMob = attacker instanceof Witch;
        
        if (!isNeutralMob) {
            return;
        }

        if (newTarget instanceof Player player && Helper.isVampire(player)) {

            VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
            int lordLevel = atts.lordLevel;
            
            if (lordLevel > 0) {
                if (attacker.getLastHurtByMob() != player) { 
                    event.setNewAboutToBeSetTarget(null);
                }
            }
        }
    }
}