package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;
import java.util.HashMap;

public class DomainEdictStorage extends SavedData {

    private final Map<ChunkPos, Boolean> monsterProtectLordEdict = new HashMap<>(); 
    private final Map<ChunkPos, Boolean> forbidOutsiderFeedingEdict = new HashMap<>(); 
    private static final boolean EDICIT_DEFAULT_STATUS = false;

    public DomainEdictStorage() {}
    public static DomainEdictStorage get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                DomainEdictStorage::new,
                DomainEdictStorage::load
            ),
            "vtm_domain_edicts"
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag domainList = new ListTag();
        for (Map.Entry<ChunkPos, Boolean> entry : monsterProtectLordEdict.entrySet()) {
            ChunkPos cp = entry.getKey();
            CompoundTag domainTag = new CompoundTag();

            domainTag.putInt("centerX", cp.x);
            domainTag.putInt("centerZ", cp.z);
            domainTag.putBoolean("MonsterProtectLord", entry.getValue());
            domainTag.putBoolean("ForbidOutsiderFeeding", forbidOutsiderFeedingEdict.getOrDefault(cp, EDICIT_DEFAULT_STATUS));
    domainList.add(domainTag);
        }

        tag.put("DomainEdicts", domainList);
        return tag;
    }

    public static DomainEdictStorage load(CompoundTag tag, HolderLookup.Provider provider) {
        DomainEdictStorage storage = new DomainEdictStorage();

        ListTag domainList = tag.getList("DomainEdicts", Tag.TAG_COMPOUND);
        for (Tag t : domainList) {
            CompoundTag domainTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(domainTag.getInt("centerX"), domainTag.getInt("centerZ"));

            if (domainTag.contains("MonsterProtectLord", Tag.TAG_BYTE)) {
                storage.monsterProtectLordEdict.put(cp, domainTag.getBoolean("MonsterProtectLord"));
            }
            if (domainTag.contains("ForbidOutsiderFeeding", Tag.TAG_BYTE)) {
                storage.forbidOutsiderFeedingEdict.put(cp, domainTag.getBoolean("ForbidOutsiderFeeding"));
            }
        }

        return storage;
    }
    
    public boolean isMonsterProtectLordEnabled(ChunkPos centerKey) {
        return monsterProtectLordEdict.getOrDefault(centerKey, EDICIT_DEFAULT_STATUS);
    }

    public void setMonsterProtectLordEnabled(ChunkPos centerKey, boolean enabled) {
        if (enabled == EDICIT_DEFAULT_STATUS) {
            monsterProtectLordEdict.remove(centerKey);
        } else {
            monsterProtectLordEdict.put(centerKey, enabled);
        }
        this.setDirty();
    }

    public boolean isForbidOutsiderFeedingEnabled(ChunkPos centerKey) {
        return forbidOutsiderFeedingEdict.getOrDefault(centerKey, EDICIT_DEFAULT_STATUS);
    }

    public void setForbidOutsiderFeedingEnabled(ChunkPos centerKey, boolean enabled) {
        if (enabled == EDICIT_DEFAULT_STATUS) {
            forbidOutsiderFeedingEdict.remove(centerKey);
        } else {
            forbidOutsiderFeedingEdict.put(centerKey, enabled);
        }
        this.setDirty();
    }

    public void removeEdictsForDomain(ChunkPos centerKey) {
        monsterProtectLordEdict.remove(centerKey);
        forbidOutsiderFeedingEdict.remove(centerKey);
        this.setDirty();
    }
}