package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class DeadAdvisorStorage extends SavedData {
    private final Map<ChunkPos, List<StoredAdvisorData>> deadAdvisorData = new HashMap<>();

    public DeadAdvisorStorage() {}

    public static DeadAdvisorStorage load(CompoundTag nbt, HolderLookup.Provider provider) {
        DeadAdvisorStorage storage = new DeadAdvisorStorage();
        ListTag domainList = nbt.getList("DeadAdvisorsByDomain", Tag.TAG_COMPOUND);
        
        for (Tag tag : domainList) {
            CompoundTag domainTag = (CompoundTag) tag;
            ChunkPos pos = new ChunkPos(domainTag.getInt("centerX"), domainTag.getInt("centerZ"));
            
            ListTag advisorList = domainTag.getList("Advisors", Tag.TAG_COMPOUND);
            List<StoredAdvisorData> advisors = new ArrayList<>();
            advisorList.forEach(t -> advisors.add(StoredAdvisorData.fromNBT((CompoundTag) t)));
            
            storage.deadAdvisorData.put(pos, advisors);
        }
        return storage;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        ListTag domainList = new ListTag();
        deadAdvisorData.forEach((pos, list) -> {
            if (list.isEmpty()) return;
            
            CompoundTag domainTag = new CompoundTag();
            domainTag.putInt("centerX", pos.x);
            domainTag.putInt("centerZ", pos.z);
            
            ListTag advisorList = new ListTag();
            list.forEach(data -> advisorList.add(data.toNBT()));
            
            domainTag.put("Advisors", advisorList);
            domainList.add(domainTag);
        });
        nbt.put("DeadAdvisorsByDomain", domainList);
        return nbt;
    }

    public void addDeadAdvisor(ChunkPos pos, StoredAdvisorData data) {
        deadAdvisorData.computeIfAbsent(pos, k -> new ArrayList<>()).add(data);
        setDirty();
    }

    public void removeDeadAdvisor(ChunkPos pos, int index) {
        List<StoredAdvisorData> list = deadAdvisorData.get(pos);
        if (list != null && index >= 0 && index < list.size()) {
            list.remove(index);
            if (list.isEmpty()) deadAdvisorData.remove(pos);
            setDirty();
        }
    }

    public List<StoredAdvisorData> getDAFD(ChunkPos pos) {
        return Collections.unmodifiableList(deadAdvisorData.getOrDefault(pos, List.of()));
    }

    public static DeadAdvisorStorage get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(DeadAdvisorStorage::new, DeadAdvisorStorage::load),
            "vtm_dead_advisor_storage"
        );
    }
}