package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.daanlokdrog.vampirismthemasquerade.ModSoundEvents;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 0-30: Key 0 (Safe/Silent)
 * 31-70: Key 1 (Medium Panic)
 * 71-100: Key 2 (High Panic)
 */
public class VillageAmbientSoundRegistry {

    private static List<SoundEvent> toSoundList(Stream<DeferredHolder<SoundEvent, SoundEvent>> holders) {
        return holders.map(DeferredHolder::get)
                      .collect(Collectors.toList());
    }

    private static final List<SoundEvent> DAY_SAFE_SOUNDS = toSoundList(Stream.of(
        ModSoundEvents.VILLAGE_DAY_SAFE1, ModSoundEvents.VILLAGE_DAY_SAFE2, 
        ModSoundEvents.VILLAGE_DAY_SAFE3, ModSoundEvents.VILLAGE_DAY_SAFE4,
        ModSoundEvents.VILLAGE_DAY_SAFE5, ModSoundEvents.VILLAGE_DAY_SAFE6,
        ModSoundEvents.VILLAGE_DAY_SAFE7, ModSoundEvents.VILLAGE_DAY_SAFE8,
        ModSoundEvents.VILLAGE_DAY_SAFE9, ModSoundEvents.VILLAGE_DAY_SAFE10,
        ModSoundEvents.VILLAGE_DAY_SAFE11, ModSoundEvents.VILLAGE_DAY_SAFE12,
        ModSoundEvents.VILLAGE_DAY_SAFE13, ModSoundEvents.VILLAGE_DAY_SAFE14,
        ModSoundEvents.VILLAGE_DAY_SAFE15, ModSoundEvents.VILLAGE_DAY_SAFE16,
        ModSoundEvents.VILLAGE_DAY_SAFE17, ModSoundEvents.VILLAGE_DAY_SAFE18,
        ModSoundEvents.VILLAGE_DAY_SAFE19, ModSoundEvents.VILLAGE_DAY_SAFE20,
        ModSoundEvents.VILLAGE_DAY_SAFE21, ModSoundEvents.VILLAGE_DAY_SAFE22,
        ModSoundEvents.VILLAGE_DAY_SAFE23
    ));
    
    private static final List<SoundEvent> DAY_MEDIUM_SOUNDS = toSoundList(Stream.of(
        ModSoundEvents.VILLAGE_DAY_LOW1, ModSoundEvents.VILLAGE_DAY_LOW2, 
        ModSoundEvents.VILLAGE_DAY_LOW3, ModSoundEvents.VILLAGE_DAY_LOW4,
        ModSoundEvents.VILLAGE_DAY_LOW5, ModSoundEvents.VILLAGE_DAY_LOW6,
        ModSoundEvents.VILLAGE_DAY_LOW7, ModSoundEvents.VILLAGE_DAY_LOW8
    ));

    private static final List<SoundEvent> DAY_HIGH_SOUNDS = toSoundList(Stream.of(
        ModSoundEvents.VILLAGE_DAY_HIGH1, ModSoundEvents.VILLAGE_DAY_HIGH2, 
        ModSoundEvents.VILLAGE_DAY_HIGH3, ModSoundEvents.VILLAGE_DAY_HIGH4,
        ModSoundEvents.VILLAGE_DAY_HIGH5, ModSoundEvents.VILLAGE_DAY_HIGH6,
        ModSoundEvents.VILLAGE_DAY_HIGH7, ModSoundEvents.VILLAGE_DAY_HIGH8
    ));

    private static final List<SoundEvent> NIGHT_MEDIUM_SOUNDS = toSoundList(Stream.of(
        ModSoundEvents.VILLAGE_NIGHT_LOW1, ModSoundEvents.VILLAGE_NIGHT_LOW2, 
        ModSoundEvents.VILLAGE_NIGHT_LOW3, ModSoundEvents.VILLAGE_NIGHT_LOW4
    ));

    private static final List<SoundEvent> NIGHT_HIGH_SOUNDS = toSoundList(Stream.of(
        ModSoundEvents.VILLAGE_NIGHT_HIGH1, ModSoundEvents.VILLAGE_NIGHT_HIGH2, 
        ModSoundEvents.VILLAGE_NIGHT_HIGH3, ModSoundEvents.VILLAGE_NIGHT_HIGH4,
        ModSoundEvents.VILLAGE_NIGHT_HIGH5, ModSoundEvents.VILLAGE_NIGHT_HIGH6,
        ModSoundEvents.VILLAGE_NIGHT_HIGH7, ModSoundEvents.VILLAGE_NIGHT_HIGH8
    ));

    private static final Map<Integer, List<SoundEvent>> DAY_SOUND_MAP = Map.of(
        0, DAY_SAFE_SOUNDS,
        1, DAY_MEDIUM_SOUNDS,
        2, DAY_HIGH_SOUNDS 
    );

    private static final Map<Integer, List<SoundEvent>> NIGHT_SOUND_MAP = Map.of(
        1, NIGHT_MEDIUM_SOUNDS,
        2, NIGHT_HIGH_SOUNDS
    );

    public static SoundEvent getRandomAmbientSound(ServerLevel level, int panicLevel) {
        RandomSource random = level.random;
        boolean isDay = level.isDay();
        int panicKey;
        if (panicLevel <= 30) {
            panicKey = 0;
        } else if (panicLevel <= 70) {
            panicKey = 1;
        } else {
            panicKey = 2;
        }
        if (!isDay && panicKey == 0) {
            return null;
        }

        Map<Integer, List<SoundEvent>> soundMap = isDay ? DAY_SOUND_MAP : NIGHT_SOUND_MAP;
        List<SoundEvent> potentialSounds = soundMap.get(panicKey);
        if (potentialSounds != null && !potentialSounds.isEmpty()) {
            return potentialSounds.get(random.nextInt(potentialSounds.size()));
        }

        return null;
    }
}