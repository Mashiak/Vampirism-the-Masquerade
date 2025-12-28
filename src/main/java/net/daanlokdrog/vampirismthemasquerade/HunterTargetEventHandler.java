package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.world.LevelFog;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.AbstractGolem;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

@EventBusSubscriber
public class HunterTargetEventHandler {

    @SubscribeEvent
    public static void onHunterTarget(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        boolean isHunter = attacker instanceof HunterBaseEntity;
        boolean isGolem = attacker instanceof AbstractGolem;
        if (!isHunter && !isGolem) return;

        if (isHunter && newTarget instanceof VampireTaskMasterEntity) {
            event.setNewAboutToBeSetTarget(null);
            return;
        }

        if (newTarget instanceof ConvertedVillagerEntity vampireVillager) {
            boolean inFog = LevelFog.get(vampireVillager.level()).isInsideArtificialVampireFogArea(vampireVillager.blockPosition());
            
            if (!inFog) {
                event.setNewAboutToBeSetTarget(null);
                return;
            }
            return;
        }

        if (!(newTarget instanceof Player player) || !Helper.isVampire(player)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        boolean inFog = LevelFog.get(player.level()).isInsideArtificialVampireFogArea(player.blockPosition());
        boolean inVampireBiome = Helper.isEntityInVampireBiome(player);
        if (inFog || inVampireBiome) {
            return;
        }

        if (vars.exposure < 40) {
            LivingEntity lastHurt = player.getLastHurtMob();
            if (lastHurt == null || !lastHurt.equals(attacker)) {
                event.setNewAboutToBeSetTarget(null);
            }
        }
    }
}


