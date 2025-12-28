package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.common.EventBusSubscriber;

import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData;
import net.daanlokdrog.vampirismthemasquerade.VillageMarker;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

@EventBusSubscriber
public class VillageAmbientSoundPlayer {
    public static final SoundSource VILLAGE_NOISE_SOURCE;

  static {
        SoundSource source;
        try {
            source = SoundSource.valueOf("VILLAGE_NOISE");
        } catch (IllegalArgumentException e) {
            // System.err.println("Mixin failed for SoundSource");
            // source = SoundSource.AMBIENT;
            throw new RuntimeException(e);
        }
        VILLAGE_NOISE_SOURCE = source;
    }

    private static final int LOW_THRESHOLD = 30;
    private static final int MED_THRESHOLD = 70;
    private static final int LOW_MIN_DAY_TICKS = 20 * 2;
    private static final int LOW_MAX_DAY_TICKS = 20 * 4;
    private static final double LOW_CHANCE = 1.0;
    private static final float LOW_VOL = 0.5f;
    private static final int MED_MIN_DAY_TICKS = 20 * 4;
    private static final int MED_MAX_DAY_TICKS = 20 * 8;
    private static final float MED_VOL_DAY = 0.7f;
    private static final int MED_MIN_NIGHT_TICKS = 20 * 6;
    private static final int MED_MAX_NIGHT_TICKS = 20 * 12;
    private static final float MED_VOL_NIGHT = 0.6f;
    private static final double MED_CHANCE = 0.75;
    private static final int HIGH_MIN_DAY_TICKS = 20 * 3;
    private static final int HIGH_MAX_DAY_TICKS = 20 * 6;
    private static final float HIGH_VOL_DAY = 0.1f;
    private static final int HIGH_MIN_NIGHT_TICKS = 20 * 1;
    private static final int HIGH_MAX_NIGHT_TICKS = 20 * 3;
    private static final float HIGH_VOL_NIGHT = 0.9f;
    private static final double HIGH_CHANCE = 0.9;
    private static final int MIN_RAD = 10;
    private static final int MAX_RAD = 20;
    private static final int HIGH_MIN_RAD = 16;
    private static final int HIGH_MAX_RAD = 24;
    private static final int MAX_DIST = 64; 

    private static final Map<UUID, Long> NEXT_CHECK_TIME = new HashMap<>(); 

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = (ServerLevel) player.level();
        
        if (!MasqueradeConfigConfiguration.VILLAGE_FX.get()) {
            return;
        }

        RandomSource random = level.random;
        long gameTime = level.getGameTime();
        UUID playerId = player.getUUID();
        BlockPos playerPos = player.blockPosition();
        
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        ChunkPos playerChunk = new ChunkPos(playerPos);
        
        ChunkPos centerKey = data.getCenterKeyForChunk(playerChunk);
        
        if (centerKey == null) {
            NEXT_CHECK_TIME.remove(playerId);
            return;
        }
        
        BlockPos villageCenterPos = data.getVillageCenter(centerKey);

        if (villageCenterPos == null || playerPos.distSqr(villageCenterPos) > MAX_DIST * MAX_DIST) {
            NEXT_CHECK_TIME.remove(playerId);
            return;
        }

        double panicLevel = data.getPanicForChunk(centerKey); 
        boolean isNight = level.isNight();
        
        final int minIntervalTicks;
        final int maxIntervalTicks;
        final double playChance;
        final float baseVolume;

        if (panicLevel <= LOW_THRESHOLD) { 
            minIntervalTicks = LOW_MIN_DAY_TICKS;
            maxIntervalTicks = LOW_MAX_DAY_TICKS;
            playChance = LOW_CHANCE;
            baseVolume = LOW_VOL;
            
        } else if (panicLevel <= MED_THRESHOLD) {
            playChance = MED_CHANCE;

            if (isNight) {
                minIntervalTicks = MED_MIN_NIGHT_TICKS;
                maxIntervalTicks = MED_MAX_NIGHT_TICKS;
                baseVolume = MED_VOL_NIGHT;
            } else {
                minIntervalTicks = MED_MIN_DAY_TICKS;
                maxIntervalTicks = MED_MAX_DAY_TICKS;
                baseVolume = MED_VOL_DAY;
            }
        } else { 
            playChance = HIGH_CHANCE;

            if (isNight) {
                minIntervalTicks = HIGH_MIN_NIGHT_TICKS;
                maxIntervalTicks = HIGH_MAX_NIGHT_TICKS;
                baseVolume = HIGH_VOL_NIGHT;
            } else {
                minIntervalTicks = HIGH_MIN_DAY_TICKS;
                maxIntervalTicks = HIGH_MAX_DAY_TICKS;
                baseVolume = HIGH_VOL_DAY;
            }
        }
        
        long nextCheckTime = NEXT_CHECK_TIME.getOrDefault(playerId, gameTime);

        if (gameTime < nextCheckTime) {
            return;
        }

        int randomInterval = random.nextInt(maxIntervalTicks - minIntervalTicks + 1) + minIntervalTicks;
        NEXT_CHECK_TIME.put(playerId, gameTime + randomInterval);
        
        if (random.nextDouble() >= playChance) {
            return;
        }

        SoundEvent soundToPlay = VillageAmbientSoundRegistry.getRandomAmbientSound(level, (int) panicLevel);

        if (soundToPlay != null) {
            playSound(player, level, soundToPlay, baseVolume, panicLevel);
        }
    }

    private static void playSound(ServerPlayer player, ServerLevel level, SoundEvent soundToPlay, float baseVolume, double panicLevel) {
        RandomSource random = level.random;
        BlockPos center = player.blockPosition();

        final int minRadius;
        final int maxRadius;
        
        if (panicLevel > MED_THRESHOLD) { 
            minRadius = HIGH_MIN_RAD;
            maxRadius = HIGH_MAX_RAD;
        } else {
            minRadius = MIN_RAD;
            maxRadius = MAX_RAD;
        }
        
        int randomRadius = random.nextInt(maxRadius - minRadius + 1) + minRadius;

        double angle = random.nextDouble() * 2 * Math.PI;

        int x = center.getX() + (int) (randomRadius * Math.cos(angle));
        int y = center.getY() + random.nextInt(5) - 2; 
        int z = center.getZ() + (int) (randomRadius * Math.sin(angle));
        BlockPos pos = new BlockPos(x, y, z);
        
        float finalVolume = baseVolume + random.nextFloat() * 0.2f;

        final SoundSource source = VILLAGE_NOISE_SOURCE;

        level.playSound(
            null, 
            pos, 
            soundToPlay, 
            source, 
            finalVolume, 
            0.9f + random.nextFloat() * 0.2f
        );
    }
}
