package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.LevelFog;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModMobEffects;
import net.daanlokdrog.vampirismthemasquerade.util.NoObserverUtil;

import java.util.List;

public class HeartBloodFoodWitness {

    private static final String VO = "VTM_IsObserverVillager";
    private static final String HO = "VTM_IsObserverHunter";

    private static int getExposureIncrease(Player player) {
        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int vampireLevel = atts.vampireLevel;
        int lordLevel = atts.lordLevel;

        if (vampireLevel >= 10 && lordLevel == 0) return 45;
        else if (lordLevel > 0) return 50;
        else return 40;
    }

    public static void handleWitness(Player player) {
        if (player.level().isClientSide) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return;

        if (player.hasEffect(VampirismTheMasqueradeModMobEffects.DEVIL_OF_THE_WORLD)) return;
        if (player.hasEffect(MobEffects.INVISIBILITY)) return;

        if (Helper.isEntityInVampireBiome(player)) {
            double forestFactor = MasqueradeConfigConfiguration.EXPOSURE_FACTOR_IN_VAMPIRE_FOREST.get();
            if (forestFactor <= 0) return;
        }

        boolean inFog = LevelFog.get(level).isInsideArtificialVampireFogArea(player.blockPosition());
        double radius = inFog ? 8.0 : 16.0; 
        int maxObservers = inFog ? 5 : 10;

        List<LivingEntity> observers = level.getEntitiesOfClass(
            LivingEntity.class,
            player.getBoundingBox().inflate(radius),
            e -> {
                if (NoObserverUtil.isNoObserver(e)) {
                    return false;
                }

                return (e.getPersistentData().getBoolean(VO) && !e.isSleeping() && e.hasLineOfSight(player))
                    || (e.getPersistentData().getBoolean(HO) && e.hasLineOfSight(player));
            }
        );

        if (!observers.isEmpty()) {
            int deltaExposure = getExposureIncrease(player);
            if (inFog) {
                double fogFactor = MasqueradeConfigConfiguration.EXPOSURE_FACTOR_IN_VAMPIRE_FOG.get();
                deltaExposure = (int) Math.round(deltaExposure * fogFactor);
            }
            vars.exposure = Math.min(100, vars.exposure + deltaExposure);
            vars.markSyncDirty();

            if (!inFog && !Helper.isEntityInVampireBiome(player)) {
                double panicBase = 0;
                for (LivingEntity e : observers) {
                    if (e.getPersistentData().getBoolean(VO)) {
                        panicBase += 5;
                    }
                }

                if (panicBase > 0) {
                    MasqueradeVillageData data = MasqueradeVillageData.get(level);
                    ChunkPos cp = new ChunkPos(player.blockPosition());
                    ChunkPos centerKey = data.getCenterKeyForChunk(cp);
                    if (centerKey != null) {
                        BlockPos centerPos = data.getVillageCenter(centerKey);
                        if (centerPos != null) {
                            double deltaPanic = PanicFactory.calculatePanicChange(level, centerPos, panicBase);
                            data.addPanicForChunk(centerKey, deltaPanic);
                        }
                    }
                }
            }
        }
    }

    public static void execute(Player player) {
        handleWitness(player);
    }
}


