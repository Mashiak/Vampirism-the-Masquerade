package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import net.daanlokdrog.vampirismthemasquerade.VillageHelper;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;


public class VillageMarker {

    private static long lastScanTimeMs = 0;
    private static boolean isWorkPoi(Holder<PoiType> holder) {
        return holder.is(PoiTypes.ARMORER)
            || holder.is(PoiTypes.BUTCHER)
            || holder.is(PoiTypes.CARTOGRAPHER)
            || holder.is(PoiTypes.CLERIC)
            || holder.is(PoiTypes.FARMER)
            || holder.is(PoiTypes.FISHERMAN)
            || holder.is(PoiTypes.FLETCHER)
            || holder.is(PoiTypes.LEATHERWORKER)
            || holder.is(PoiTypes.LIBRARIAN)
            || holder.is(PoiTypes.MASON)
            || holder.is(PoiTypes.SHEPHERD)
            || holder.is(PoiTypes.TOOLSMITH)
            || holder.is(PoiTypes.WEAPONSMITH);
    }

public static void scanAndMarkArea(ServerLevel level, BlockPos scanPos, int poiRadius) {

        if (!VillageHelper.hasVillageNearby(level, scanPos, poiRadius)) return;

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        ChunkPos scanChunk = new ChunkPos(scanPos);
        ChunkPos centerKey = data.getCenterKeyForChunk(scanChunk);

        if (centerKey == null) {
            centerKey = scanChunk;
            BlockPos fixedCenter = calculateVillageCenter(level, scanPos, poiRadius);
            data.setVillageCenter(centerKey, fixedCenter);
            int chunkRadius = Math.max(1, poiRadius / 16);
            for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                    ChunkPos cp = new ChunkPos(centerKey.x + dx, centerKey.z + dz);
                    data.setVillageChunk(cp, true);
                    data.mapChunkToCenter(cp, centerKey); 
                }
            }
        }

        var pois = level.getPoiManager().getInRange(
            holder -> holder.is(PoiTypes.HOME) || holder.is(PoiTypes.MEETING) || isWorkPoi(holder),
            scanPos,
            poiRadius,
            PoiManager.Occupancy.ANY
        ).toList();
        
        Map<ChunkPos, Integer> poiCounts = new HashMap<>();

        for (var poi : pois) {
            ChunkPos cp = new ChunkPos(poi.getPos());
            if (!data.isVillageChunk(cp)) {
                data.setVillageChunk(cp, true);
            }
            data.mapChunkToCenter(cp, centerKey); 
            ChunkPos actualCenterKey = data.getCenterKeyForChunk(cp);
            if (actualCenterKey != null) {
                poiCounts.put(actualCenterKey, poiCounts.getOrDefault(actualCenterKey, 0) + 1);
            }
        }

        Set<ChunkPos> foundCenters = new HashSet<>();
        int chunkRadius = Math.max(1, poiRadius / 16);
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                ChunkPos cp = new ChunkPos(scanChunk.x + dx, scanChunk.z + dz);
                ChunkPos ck = data.getCenterKeyForChunk(cp);
                if (ck != null) {
                    foundCenters.add(ck);
                }
            }
        }

        mergeVillageCenters(data, foundCenters, poiCounts);

        data.setDirty();
    }

    public static void mergeVillageCenters(MasqueradeVillageData data, Set<ChunkPos> centers, Map<ChunkPos, Integer> poiCounts) {
        if (centers == null || centers.size() <= 1) return;
        
        ChunkPos mainCenterKey = null;
        int maxPoi = -1;
        if (poiCounts != null && !poiCounts.isEmpty()) {
            for (ChunkPos cp : centers) {
                int count = poiCounts.getOrDefault(cp, 0);
                if (count > maxPoi) {
                    maxPoi = count;
                    mainCenterKey = cp;
                }
            }
        }
        if (mainCenterKey == null) {
            mainCenterKey = centers.iterator().next();
        }

        BlockPos mainCenterPos = data.getVillageCenter(mainCenterKey);
        if (mainCenterPos == null) return;
        for (ChunkPos otherCenterKey : centers) {
            if (otherCenterKey.equals(mainCenterKey)) continue;
            
            Set<ChunkPos> chunksToRemap = data.getMappedChunksForCenter(otherCenterKey); 
            
            if (!chunksToRemap.isEmpty()) {
                for (ChunkPos chunk : chunksToRemap) {
                    data.mapChunkToCenter(chunk, mainCenterKey);
                }
            }

            data.removeVillageCenter(otherCenterKey);
        }
    }

    private static BlockPos calculateVillageCenter(ServerLevel level, BlockPos center, int radius) {
        var bells = level.getPoiManager().getInRange(
            poiType -> poiType == PoiTypes.MEETING,
            center,
            radius,
            PoiManager.Occupancy.ANY
        ).toList();
        if (!bells.isEmpty()) {
            return bells.get(0).getPos();
        }

        var beds = level.getPoiManager().getInRange(
            poiType -> poiType == PoiTypes.HOME,
            center,
            radius,
            PoiManager.Occupancy.ANY
        ).toList();
        if (!beds.isEmpty()) {
            int avgX = beds.stream().mapToInt(p -> p.getPos().getX()).sum() / beds.size();
            int avgY = beds.stream().mapToInt(p -> p.getPos().getY()).sum() / beds.size();
            int avgZ = beds.stream().mapToInt(p -> p.getPos().getZ()).sum() / beds.size();
            return new BlockPos(avgX, avgY, avgZ);
        }


        return center;
    }

    public static boolean isVillageChunk(ServerLevel level, BlockPos pos) {
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        return data.isVillageChunk(new ChunkPos(pos));
    }

    public static void debugExpandVillage(ServerLevel level, BlockPos centerPos, ChunkPos centerKey, int radiusChunks, ServerPlayer player) {
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        data.setVillageCenter(centerKey, centerPos);
        int mappedCount = 0;

        for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
            for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                ChunkPos cp = new ChunkPos(centerKey.x + dx, centerKey.z + dz);
                data.setVillageChunk(cp, true);
                data.mapChunkToCenter(cp, centerKey); 
                mappedCount++;
            }
        }

        data.setDirty();
    }
}