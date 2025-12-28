package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

/**
 * Core part
 * it works with VillageMarker and VillageHelper. A new village data system has been established.
 */

public class MasqueradeVillageData extends SavedData {

    private final Map<ChunkPos, Double> panicValues = new HashMap<>();
    private final Set<ChunkPos> villageChunks = new HashSet<>();

    private final Map<ChunkPos, BlockPos> villageCenters = new HashMap<>();

    private final Map<ChunkPos, ChunkPos> chunkToCenterKey = new HashMap<>();

    private final Map<ChunkPos, Set<ChunkPos>> centerToMappedChunks = new HashMap<>();
    
    private final Map<ChunkPos, Integer> hunterCounts = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag panicList = new ListTag();
        for (Map.Entry<ChunkPos, Double> entry : panicValues.entrySet()) {
            ChunkPos cp = entry.getKey();
            CompoundTag panicTag = new CompoundTag();
            panicTag.putInt("centerX", cp.x);
            panicTag.putInt("centerZ", cp.z);
            panicTag.putDouble("panic", entry.getValue());
            panicList.add(panicTag);
        }
        tag.put("panic_values", panicList);

        ListTag chunkList = new ListTag();
        for (ChunkPos cp : villageChunks) {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt("x", cp.x);
            chunkTag.putInt("z", cp.z);
            chunkList.add(chunkTag);
        }
        tag.put("village_chunks", chunkList);

        ListTag centerList = new ListTag();
        for (Map.Entry<ChunkPos, BlockPos> entry : villageCenters.entrySet()) {
            BlockPos pos = entry.getValue();
            CompoundTag centerTag = new CompoundTag();
            centerTag.putInt("chunkX", entry.getKey().x);
            centerTag.putInt("chunkZ", entry.getKey().z);
            centerTag.putInt("x", pos.getX());
            centerTag.putInt("y", pos.getY());
            centerTag.putInt("z", pos.getZ());
            centerList.add(centerTag);
        }
        tag.put("village_centers", centerList);

        ListTag mappingList = new ListTag();
        for (Map.Entry<ChunkPos, ChunkPos> entry : chunkToCenterKey.entrySet()) {
            ChunkPos centerKey = entry.getValue();
            CompoundTag mapTag = new CompoundTag();
            mapTag.putInt("chunkX", entry.getKey().x);
            mapTag.putInt("chunkZ", entry.getKey().z);
            mapTag.putInt("centerX", centerKey.x);
            mapTag.putInt("centerZ", centerKey.z);
            mappingList.add(mapTag);
        }
        tag.put("chunk_to_center", mappingList);
        
        ListTag hunterCountList = new ListTag();
        for (Map.Entry<ChunkPos, Integer> entry : hunterCounts.entrySet()) {
            CompoundTag hunterTag = new CompoundTag();
            hunterTag.putInt("centerX", entry.getKey().x);
            hunterTag.putInt("centerZ", entry.getKey().z);
            hunterTag.putInt("count", entry.getValue());
            hunterCountList.add(hunterTag);
        }
        tag.put("hunter_counts", hunterCountList);
        
        return tag;
    }

    public static MasqueradeVillageData load(CompoundTag tag, HolderLookup.Provider provider) {
        MasqueradeVillageData data = new MasqueradeVillageData();

        ListTag panicList = tag.getList("panic_values", Tag.TAG_COMPOUND);
        for (Tag t : panicList) {
            CompoundTag panicTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(panicTag.getInt("centerX"), panicTag.getInt("centerZ"));
            double panic = panicTag.getDouble("panic");
            data.panicValues.put(cp, Math.max(0.0, Math.min(100.0, panic)));
        }

        ListTag chunkList = tag.getList("village_chunks", Tag.TAG_COMPOUND);
        for (Tag t : chunkList) {
            CompoundTag chunkTag = (CompoundTag) t;
            data.villageChunks.add(new ChunkPos(chunkTag.getInt("x"), chunkTag.getInt("z")));
        }

        ListTag centerList = tag.getList("village_centers", Tag.TAG_COMPOUND);
        for (Tag t : centerList) {
            CompoundTag centerTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(centerTag.getInt("chunkX"), centerTag.getInt("chunkZ"));
            BlockPos pos = new BlockPos(centerTag.getInt("x"), centerTag.getInt("y"), centerTag.getInt("z"));
            data.villageCenters.put(cp, pos);
        }

        ListTag mappingList = tag.getList("chunk_to_center", Tag.TAG_COMPOUND);
        for (Tag t : mappingList) {
            CompoundTag mapTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(mapTag.getInt("chunkX"), mapTag.getInt("chunkZ"));
            ChunkPos centerKey = new ChunkPos(mapTag.getInt("centerX"), mapTag.getInt("centerZ"));
            data.chunkToCenterKey.put(cp, centerKey);
        }
        
        ListTag hunterCountList = tag.getList("hunter_counts", Tag.TAG_COMPOUND);
        for (Tag t : hunterCountList) {
            CompoundTag hunterTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(hunterTag.getInt("centerX"), hunterTag.getInt("centerZ"));
            data.hunterCounts.put(cp, hunterTag.getInt("count"));
        }
        
        data.rebuildCenterToMappedChunks();

        return data;
    }

    private void rebuildCenterToMappedChunks() {
        this.centerToMappedChunks.clear();
        for (Map.Entry<ChunkPos, ChunkPos> entry : chunkToCenterKey.entrySet()) {
            ChunkPos chunk = entry.getKey();
            ChunkPos centerKey = entry.getValue();
            
            centerToMappedChunks
                .computeIfAbsent(centerKey, k -> new HashSet<>())
                .add(chunk);
        }
    }


    public double getPanicForChunk(ChunkPos chunk) {
        ChunkPos centerKey = getCenterKeyForChunk(chunk);
        if (centerKey == null) return 0.0;
        return panicValues.getOrDefault(centerKey, 0.0);
    }

    public void setPanicForChunk(ChunkPos chunk, double value) {
        ChunkPos centerKey = getCenterKeyForChunk(chunk);
        if (centerKey == null) return;
        double clamped = Math.max(0.0, Math.min(100.0, value));
        panicValues.put(centerKey, clamped);
        setDirty();
    }

    public void addPanicForChunk(ChunkPos chunk, double delta) {
        ChunkPos centerKey = getCenterKeyForChunk(chunk);
        if (centerKey == null) return;
        double current = panicValues.getOrDefault(centerKey, 0.0);
        double newValue = Math.max(0.0, Math.min(100.0, current + delta));
        panicValues.put(centerKey, newValue);
        setDirty();
    }
    
    public int getHunterCount(ChunkPos centerKey) {
        return hunterCounts.getOrDefault(centerKey, 0);
    }
    
    public void setHunterCount(ChunkPos centerKey, int count) {
        if (count <= 0) {
            hunterCounts.remove(centerKey);
        } else {
            hunterCounts.put(centerKey, count);
        }
        setDirty();
    }

    public void incrementHunterCount(ChunkPos centerKey) {
        setHunterCount(centerKey, getHunterCount(centerKey) + 1);
    }

    public void decrementHunterCount(ChunkPos centerKey) {
        setHunterCount(centerKey, getHunterCount(centerKey) - 1);
    }


    public boolean isVillageChunk(ChunkPos pos) {
        return villageChunks.contains(pos);
    }

    public void setVillageChunk(ChunkPos pos, boolean value) {
        if (value) {
            if (villageChunks.add(pos)) setDirty();
        } else {
            if (villageChunks.remove(pos)) setDirty();
        }
    }

    public Set<ChunkPos> getVillageChunks() {
        return new HashSet<>(villageChunks);
    }

    public void mapChunkToCenter(ChunkPos chunk, ChunkPos centerKey) {
        
        ChunkPos oldCenterKey = chunkToCenterKey.get(chunk);
        
        if (oldCenterKey != null && !oldCenterKey.equals(centerKey)) {
            Set<ChunkPos> oldSet = centerToMappedChunks.get(oldCenterKey);
            if (oldSet != null) {
                oldSet.remove(chunk);
            }
        }
        
        if (centerKey != null) {
            chunkToCenterKey.put(chunk, centerKey);

            centerToMappedChunks
                .computeIfAbsent(centerKey, k -> new HashSet<>())
                .add(chunk);

            setDirty();
        } else if (oldCenterKey != null) {
            chunkToCenterKey.remove(chunk);
            setDirty();
        }
    }

    public void setVillageCenter(ChunkPos centerKey, BlockPos centerPos) {
        villageCenters.putIfAbsent(centerKey, centerPos);
        setDirty();
    }

    public BlockPos getVillageCenter(ChunkPos centerKey) {
        return villageCenters.get(centerKey);
    }

    public ChunkPos getCenterKeyForChunk(ChunkPos chunk) {
        return chunkToCenterKey.get(chunk);
    }
    
    public Set<ChunkPos> getMappedChunksForCenter(ChunkPos centerKey) {
        Set<ChunkPos> chunks = centerToMappedChunks.get(centerKey);
        return chunks != null ? new HashSet<>(chunks) : Collections.emptySet();
    }

    public void removeVillageCenter(ChunkPos centerKey) {
        villageCenters.remove(centerKey);
        panicValues.remove(centerKey); 
        hunterCounts.remove(centerKey); 

        Set<ChunkPos> mappedChunks = centerToMappedChunks.remove(centerKey); 
        
        if (mappedChunks != null) {
            for (ChunkPos chunk : mappedChunks) {
                chunkToCenterKey.remove(chunk); 
            }
        }
        
        setDirty();
    }

    public void removeChunkMapping(ChunkPos chunk) {
        mapChunkToCenter(chunk, null);
    }
    
    public Set<ChunkPos> getAllVillageCenters() {
        return villageCenters.keySet();
    }
    
    public static MasqueradeVillageData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                MasqueradeVillageData::new,
                MasqueradeVillageData::load
            ),
            "masquerade_village_data"
        );
    }
}