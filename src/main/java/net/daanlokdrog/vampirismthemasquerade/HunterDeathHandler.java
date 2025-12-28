package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModMobEffects;

import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData;
import net.daanlokdrog.vampirismthemasquerade.HuntEventManager;
import static net.daanlokdrog.vampirismthemasquerade.VillageHunterSpawner.CENTER_KEY_X_TAG; 
import static net.daanlokdrog.vampirismthemasquerade.VillageHunterSpawner.CENTER_KEY_Z_TAG;

@EventBusSubscriber
public class HunterDeathHandler {

    private static final String VG = "village_guard";

    @SubscribeEvent
    public static void onHunterDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        boolean isBasic = entity instanceof BasicHunterEntity;
        boolean isAdvanced = entity instanceof AdvancedHunterEntity;
        
        if (isBasic || isAdvanced) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
                if (entity.level().isClientSide) return;
                
                VampirismTheMasqueradeModVariables.PlayerVariables vars =
                    player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
                if (vars != null) {
                    if (entity.getPersistentData().getBoolean("masquerade_spawned")) {
                        vars.spawnedHunters = Math.max(0, vars.spawnedHunters - 1);
                        vars.huntKillCounter++;
                    }

                    if (isBasic) {
                        vars.huntValue = Math.max(0.0, vars.huntValue - 2.0);
                    } else if (isAdvanced) {
                        vars.huntValue = Math.max(0.0, vars.huntValue - 15.0);
                    }

                    HuntEventManager.updateProgress(player);

                    if (vars.huntValue <= 0.0 && vars.huntBegining) {
                        HuntEventManager.setTitle(player, "vampirism_the_masquerade_huntFailed");

                        player.addEffect(new MobEffectInstance(
                            VampirismTheMasqueradeModMobEffects.DEVIL_OF_THE_WORLD,
                            108000, 0, false, true
                        ));
                    }

                    vars.markSyncDirty();
                }
            }
        }
        
        if (entity.level().isClientSide) {
            return;
        }

        if (entity.getTags().contains(VG)) {
            
            if (!(entity.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            int keyX = entity.getPersistentData().getInt(CENTER_KEY_X_TAG);
            int keyZ = entity.getPersistentData().getInt(CENTER_KEY_Z_TAG);

            if (entity.getPersistentData().contains(CENTER_KEY_X_TAG) && entity.getPersistentData().contains(CENTER_KEY_Z_TAG)) {
                ChunkPos centerKey = new ChunkPos(keyX, keyZ);
                MasqueradeVillageData data = MasqueradeVillageData.get(serverLevel);
                data.decrementHunterCount(centerKey);
            }
        }
    }
}

