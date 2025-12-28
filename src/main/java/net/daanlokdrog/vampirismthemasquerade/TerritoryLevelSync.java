package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;

import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes; 
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;


import net.daanlokdrog.vampirismthemasquerade.MasqueradeDomainData;
import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;

@EventBusSubscriber
public class TerritoryLevelSync {

    private static final int SYNC_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer) || serverPlayer.tickCount % SYNC_INTERVAL != 0) {
            return;
        }

        VampirismTheMasqueradeModVariables.PlayerVariables playerVars = serverPlayer.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (!playerVars.hasTerritory) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        BlockPos playerPos = serverPlayer.blockPosition();
        ChunkPos currentChunk = new ChunkPos(playerPos);
        MasqueradeVillageData villageData = MasqueradeVillageData.get(serverLevel);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(serverLevel);
        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);
        
        if (villageCenterKey == null || !domainData.isDomainClaimed(villageCenterKey)) {
            return;
        }

        String lordName = domainData.getLordName(villageCenterKey);
        String playerName = serverPlayer.getName().getString();

        if (lordName.equals(playerName)) {
            VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(serverPlayer);
            int currentVampireLevel = atts.vampireLevel;
            int currentLordLevel = atts.lordLevel;
            IPlayableFaction.TitleGender currentGender = FactionPlayerHandler.get(serverPlayer).titleGender();
            boolean currentIsLadyLord = currentGender == IPlayableFaction.TitleGender.FEMALE;
            int domainVampireLevel = domainData.getVampireLevel(villageCenterKey);
            int domainLordLevel = domainData.getLordLevel(villageCenterKey);
            boolean domainIsLadyLord = domainData.isLadyLord(villageCenterKey);
            int domainMaxAdvisors = domainData.getMaxAdvisorCount(villageCenterKey);
            boolean changed = false;

            if (currentVampireLevel != domainVampireLevel) {
                domainData.setVampireLevel(villageCenterKey, currentVampireLevel);
                changed = true;
            }
            if (currentLordLevel != domainLordLevel) {
                domainData.setLordLevel(villageCenterKey, currentLordLevel);
                changed = true;
            }
            
            if (currentIsLadyLord != domainIsLadyLord) {
                domainData.setIsLadyLord(villageCenterKey, currentIsLadyLord);
                changed = true;
            }

            int newMaxAdvisors = currentLordLevel;

            if (newMaxAdvisors != domainMaxAdvisors) {
                domainData.setMaxAdvisorCount(villageCenterKey, newMaxAdvisors);
                changed = true;
            }

            if (changed) {
                domainData.setDirty();
            }
        }
    }
}