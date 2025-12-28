package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.player.Player; 
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class MasqueradeDomainData extends SavedData {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasqueradeDomainData.class);

    private final Map<ChunkPos, String> lordUUIDs = new HashMap<>(); 
    private final Map<ChunkPos, String> lordNames = new HashMap<>(); 
    private final Map<ChunkPos, String> domainNames = new HashMap<>(); 
    private final Map<ChunkPos, Boolean> isLadyLords = new HashMap<>();
    private final Map<ChunkPos, Integer> vampireLevels = new HashMap<>();
    private final Map<ChunkPos, Integer> lordLevels = new HashMap<>();
    private final Map<ChunkPos, Double> habitabilityValues = new HashMap<>();
    private final Map<ChunkPos, Integer> guardWeaponValues = new HashMap<>();
    private final Map<ChunkPos, Integer> guardArmorValues = new HashMap<>();
    private final Map<ChunkPos, Integer> currentGuardCounts = new HashMap<>();
    private final Map<ChunkPos, Integer> maxGuardCounts = new HashMap<>();
    private final Map<ChunkPos, Integer> currentAdvisorCounts = new HashMap<>(); 
    private final Map<ChunkPos, Integer> maxAdvisorCounts = new HashMap<>(); 

    private static final int DOMAIN_RADIUS = 64; 
    private static final String DEFAULT_STRING = "";
    private static final int DEFAULT_INT = 0;
    private static final boolean DEFAULT_BOOL = false;
    private static final double DEFAULT_DOUBLE = 0.0;
    private static final double MAX_HABITABILITY = 100.0;
    private static final double MIN_HABITABILITY = 0.0;

    public static MasqueradeDomainData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                MasqueradeDomainData::new,
                MasqueradeDomainData::load
            ),
            "masquerade_domain_data" 
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag domainList = new ListTag();

        for (Map.Entry<ChunkPos, String> entry : lordNames.entrySet()) {
            ChunkPos cp = entry.getKey();
            CompoundTag domainTag = new CompoundTag();

            domainTag.putInt("centerX", cp.x);
            domainTag.putInt("centerZ", cp.z);
            domainTag.putString("LordName", entry.getValue());
            domainTag.putString("LordUUID", lordUUIDs.getOrDefault(cp, DEFAULT_STRING)); 
            domainTag.putString("DomainName", domainNames.getOrDefault(cp, DEFAULT_STRING)); 
            domainTag.putBoolean("IsLadyLord", isLadyLords.getOrDefault(cp, DEFAULT_BOOL));
            domainTag.putInt("VampireLevel", vampireLevels.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("LordLevel", lordLevels.getOrDefault(cp, DEFAULT_INT));
            domainTag.putDouble("Habitability", habitabilityValues.getOrDefault(cp, DEFAULT_DOUBLE));
            domainTag.putInt("GuardWeaponValue", guardWeaponValues.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("GuardArmorValue", guardArmorValues.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("CurrentGuardCount", currentGuardCounts.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("MaxGuardCount", maxGuardCounts.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("CurrentAdvisorCount", currentAdvisorCounts.getOrDefault(cp, DEFAULT_INT));
            domainTag.putInt("MaxAdvisorCount", maxAdvisorCounts.getOrDefault(cp, DEFAULT_INT));

            domainList.add(domainTag);
        }
        tag.put("domains", domainList);
        return tag;
    }

    public static MasqueradeDomainData load(CompoundTag tag, HolderLookup.Provider provider) {
        MasqueradeDomainData data = new MasqueradeDomainData();

        ListTag domainList = tag.getList("domains", Tag.TAG_COMPOUND);
        for (Tag t : domainList) {
            CompoundTag domainTag = (CompoundTag) t;
            ChunkPos cp = new ChunkPos(domainTag.getInt("centerX"), domainTag.getInt("centerZ"));
            data.lordNames.put(cp, domainTag.getString("LordName"));
            data.lordUUIDs.put(cp, domainTag.getString("LordUUID")); 
            data.domainNames.put(cp, domainTag.getString("DomainName")); 
            data.isLadyLords.put(cp, domainTag.getBoolean("IsLadyLord"));
            data.vampireLevels.put(cp, domainTag.getInt("VampireLevel"));
            data.lordLevels.put(cp, domainTag.getInt("LordLevel"));
            data.habitabilityValues.put(cp, domainTag.getDouble("Habitability"));
            data.guardWeaponValues.put(cp, domainTag.getInt("GuardWeaponValue"));
            data.guardArmorValues.put(cp, domainTag.getInt("GuardArmorValue"));
            data.currentGuardCounts.put(cp, domainTag.getInt("CurrentGuardCount"));
            data.maxGuardCounts.put(cp, domainTag.getInt("MaxGuardCount"));
            data.currentAdvisorCounts.put(cp, domainTag.getInt("CurrentAdvisorCount"));
            data.maxAdvisorCounts.put(cp, domainTag.getInt("MaxAdvisorCount"));
        }

        return data;
    }
    public boolean isDomainClaimed(ChunkPos centerKey) {
        return !getLordUUID(centerKey).isEmpty();
    }

    public void removeDomain(ChunkPos centerKey) {
        lordUUIDs.remove(centerKey); 
        lordNames.remove(centerKey);
        domainNames.remove(centerKey); 
        isLadyLords.remove(centerKey);
        vampireLevels.remove(centerKey);
        lordLevels.remove(centerKey);
        
        habitabilityValues.remove(centerKey);
        guardWeaponValues.remove(centerKey);
        guardArmorValues.remove(centerKey);
        currentGuardCounts.remove(centerKey);
        maxGuardCounts.remove(centerKey);
        
        currentAdvisorCounts.remove(centerKey);
        maxAdvisorCounts.remove(centerKey); 
        
        setDirty();
    }

    public void unbindLord(ChunkPos centerKey) {
        lordUUIDs.remove(centerKey);
        lordNames.remove(centerKey);
        domainNames.remove(centerKey); 
        isLadyLords.remove(centerKey);
        vampireLevels.remove(centerKey);
        lordLevels.remove(centerKey);
        
        setDirty();
    }

    public void setLordIdentity(ChunkPos centerKey, String name, String uuidString) {
        if (uuidString == null || uuidString.isEmpty() || uuidString.equals(DEFAULT_STRING)) {
            unbindLord(centerKey);
        } else {
            lordNames.put(centerKey, Optional.ofNullable(name).orElse(DEFAULT_STRING));
            lordUUIDs.put(centerKey, uuidString);
        }
        setDirty();
    }

    public String getLordUUID(ChunkPos centerKey) {
        return lordUUIDs.getOrDefault(centerKey, DEFAULT_STRING);
    }

    public Optional<UUID> getLordUUIDObject(ChunkPos centerKey) {
        String uuidString = getLordUUID(centerKey);
        if (uuidString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid UUID at {}: {}", centerKey, uuidString);
            return Optional.empty();
        }
    }

    public String getLordName(ChunkPos centerKey) {
        return lordNames.getOrDefault(centerKey, DEFAULT_STRING);
    }
    
    public void setLordName(ChunkPos centerKey, String name) {
        lordNames.put(centerKey, Optional.ofNullable(name).orElse(DEFAULT_STRING));
        setDirty();
    }

    public String getDomainName(ChunkPos centerKey) {
        return domainNames.getOrDefault(centerKey, DEFAULT_STRING);
    }

    public void setDomainName(ChunkPos centerKey, String name) {
        domainNames.put(centerKey, Optional.ofNullable(name).orElse(DEFAULT_STRING)); 
        setDirty();
    }
    
    public boolean isLadyLord(ChunkPos centerKey) {
        return isLadyLords.getOrDefault(centerKey, DEFAULT_BOOL);
    }

    public void setIsLadyLord(ChunkPos centerKey, boolean isFemale) {
        isLadyLords.put(centerKey, isFemale);
        setDirty();
    }

    public int getVampireLevel(ChunkPos centerKey) {
        return vampireLevels.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setVampireLevel(ChunkPos centerKey, int level) {
        vampireLevels.put(centerKey, Math.max(0, level)); 
        setDirty();
    }

    public int getLordLevel(ChunkPos centerKey) {
        return lordLevels.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setLordLevel(ChunkPos centerKey, int level) {
        lordLevels.put(centerKey, Math.max(0, level));
        setDirty();
    }

    public double getHabitability(ChunkPos centerKey) {
        return habitabilityValues.getOrDefault(centerKey, DEFAULT_DOUBLE);
    }

    public void setHabitability(ChunkPos centerKey, double value) {
        double clampedValue = Math.min(MAX_HABITABILITY, Math.max(MIN_HABITABILITY, value));
        habitabilityValues.put(centerKey, clampedValue);
        setDirty();
    }
    
    public void addHabitability(ChunkPos centerKey, double delta) {
        double current = getHabitability(centerKey);
        double newValue = current + delta;
        setHabitability(centerKey, newValue);
    }

    public double getEffectiveHabitability(ServerLevel level, ChunkPos centerKey) {
        double storedHabitability = this.getHabitability(centerKey);
        return storedHabitability; 
    }


    public int getGuardWeaponValue(ChunkPos centerKey) {
        return guardWeaponValues.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setGuardWeaponValue(ChunkPos centerKey, int value) {
        guardWeaponValues.put(centerKey, Math.max(0, value));
        setDirty();
    }

    public int getGuardArmorValue(ChunkPos centerKey) {
        return guardArmorValues.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setGuardArmorValue(ChunkPos centerKey, int value) {
        guardArmorValues.put(centerKey, Math.max(0, value));
        setDirty();
    }
    
    public int getCurrentGuardCount(ChunkPos centerKey) {
        return currentGuardCounts.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setCurrentGuardCount(ChunkPos centerKey, int count) {
        currentGuardCounts.put(centerKey, Math.max(0, count));
        setDirty();
    }

    public int getMaxGuardCount(ChunkPos centerKey) {
        return maxGuardCounts.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setMaxGuardCount(ChunkPos centerKey, int count) {
        maxGuardCounts.put(centerKey, Math.max(0, count)); 
        setDirty();
    }

    public int getCurrentAdvisorCount(ChunkPos centerKey) {
        return currentAdvisorCounts.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setCurrentAdvisorCount(ChunkPos centerKey, int count) {
        currentAdvisorCounts.put(centerKey, Math.max(0, count));
        setDirty();
    }

    public int getMaxAdvisorCount(ChunkPos centerKey) {
        return maxAdvisorCounts.getOrDefault(centerKey, DEFAULT_INT);
    }

    public void setMaxAdvisorCount(ChunkPos centerKey, int count) {
        maxAdvisorCounts.put(centerKey, Math.max(0, count)); 
        setDirty();
    }
}