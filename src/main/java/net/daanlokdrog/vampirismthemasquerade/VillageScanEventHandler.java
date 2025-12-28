package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

@EventBusSubscriber
public class VillageScanEventHandler {

    private static final int PL_SCAN_RADIUS = 96; 

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!MasqueradeConfigConfiguration.VILLAGE_SCAN.get()) {
            return;
        }
        
        ServerLevel serverLevel = serverPlayer.serverLevel();
        long currentTime = serverLevel.getGameTime();
        double intervalSeconds = MasqueradeConfigConfiguration.VILLAGE_SCAN_INTERVAL.get();
        long intervalTicks = (long) (intervalSeconds * 20); 
        
        if (intervalTicks > 0 && currentTime % intervalTicks != 0) {
            return;
        }
        scanVillageAroundPlayer(serverPlayer, serverLevel);
    }

    private static void scanVillageAroundPlayer(ServerPlayer player, ServerLevel level) {
        BlockPos playerPos = player.blockPosition();
        PoiManager poiMGR = level.getPoiManager();
        Optional<BlockPos> anchorPos = poiMGR.getInRange(
            poiHolder -> poiHolder.is(PoiTypes.MEETING) || poiHolder.is(PoiTypes.HOME),
            playerPos,
            PL_SCAN_RADIUS,
            PoiManager.Occupancy.ANY
        )
        .map(poiRecord -> poiRecord.getPos())
        .findFirst();

        if (anchorPos.isPresent()) {
            BlockPos anchor = anchorPos.get();
            VillageMarker.scanAndMarkArea(level, anchor, PL_SCAN_RADIUS);
        }
    }
}