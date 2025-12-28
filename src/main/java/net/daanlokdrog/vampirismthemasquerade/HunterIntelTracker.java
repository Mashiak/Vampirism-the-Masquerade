package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import de.teamlapen.vampirism.core.ModEntities; 

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent; 
import net.minecraft.ChatFormatting;

import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent; 
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import de.teamlapen.vampirism.blocks.CoffinBlock; 
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity; 
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity; 

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class HunterIntelTracker { 

    private static final Map<ChunkPos, Integer> NEST_INTEL_CACHE = new ConcurrentHashMap<>();
    
    private static final String CHUNK_INTEL_TAG = "VTM_Hunter_Intel";
    private static final String CD_TAG = "VTM_IntelCooldownTicks";
    
    private static final int CD = 10000;

    private static int getSiegeThreshold() {
        return MasqueradeConfigConfiguration.SIEGE_THRESHOLD.get().intValue();
    }

    private static float getHunterScaleFactor(ServerPlayer player) {
        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int totalLevel = atts.vampireLevel + atts.lordLevel;
        return 1.0f + (totalLevel * 0.10f);
    }

    private static void applyHunterScaling(LivingEntity hunter, float scaleFactor) {

        if (hunter.getAttribute(Attributes.MAX_HEALTH) != null) {
            double newHealth = hunter.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * scaleFactor;
            hunter.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
            hunter.setHealth((float)newHealth);
        }
        if (hunter.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            double newDamage = hunter.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * scaleFactor;
            hunter.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newDamage);
        }
        if (hunter.getAttribute(Attributes.ARMOR) != null) {
             double newArmor = hunter.getAttribute(Attributes.ARMOR).getBaseValue() * scaleFactor;
            hunter.getAttribute(Attributes.ARMOR).setBaseValue(newArmor);
        }
    }

    private static void triggerHunterSiege(ServerPlayer player, Level level, ChunkPos chunkPos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        RandomSource random = serverLevel.getRandom();

        float scaleFactor = getHunterScaleFactor(player);
        int spawnedCount = 0;
        int basicCount = random.nextInt(3) + 3; 
        boolean spawnAdvanced = random.nextFloat() < 0.50f;

        MutableComponent warning = Component.translatable("vtm.siege_warning")
            .withStyle(ChatFormatting.DARK_PURPLE); 
        player.sendSystemMessage(warning);
        
        serverLevel.playSound(null, player.blockPosition(),
            SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 6.0F, 1.0F);

        for (int i = 0; i < basicCount; i++) {
            double offsetX = (random.nextInt(41) - 20);
            double offsetZ = (random.nextInt(41) - 20);

            BlockPos pos = serverLevel.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlockPos.containing(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ)
            );
            
            BasicHunterEntity hunter = ModEntities.HUNTER.get().spawn(serverLevel, pos, MobSpawnType.EVENT);
            
            if (hunter != null) {
                applyHunterScaling(hunter, scaleFactor);
                spawnedCount++;
            }
        }

        if (spawnAdvanced) {
            double offsetX = (random.nextInt(41) - 20);
            double offsetZ = (random.nextInt(41) - 20);

            BlockPos pos = serverLevel.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlockPos.containing(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ)
            );

            AdvancedHunterEntity hunter = ModEntities.ADVANCED_HUNTER.get().spawn(serverLevel, pos, MobSpawnType.EVENT);
            
            if (hunter != null) {
                applyHunterScaling(hunter, scaleFactor);
                spawnedCount++;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) { 
        if (event.getEntity() instanceof ServerPlayer player) {
            CompoundTag persistentData = player.getPersistentData();
            int cooldown = persistentData.getInt(CD_TAG);
            
            if (cooldown > 0) {
                persistentData.putInt(CD_TAG, cooldown - 1);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        
        BlockPos sleepPos = player.getSleepingPos().orElse(null);
        if (sleepPos == null) {
            return;
        }

        Block block = player.level().getBlockState(sleepPos).getBlock();
        if (!(block instanceof CoffinBlock)) {
            return;
        }

        PlayerVariables playerVars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (playerVars == null) {
            return;
        }
        
        CompoundTag persistentData = player.getPersistentData();
        int currentCooldown = persistentData.getInt(CD_TAG);

        if (currentCooldown > 0) {
            return;
        }

        double currentExposure = playerVars.exposure;
        int intelGained = (int) Math.floor(currentExposure / 2.0);

        if (intelGained > 0) {
            Level level = player.level();
            ChunkPos chunkPos = player.chunkPosition();
            
            int currentNestIntel = getChunkIntel(level, chunkPos);
            int newNestIntel = currentNestIntel + intelGained;
            
            setChunkIntel(level, chunkPos, newNestIntel);
            persistentData.putInt(CD_TAG, CD);
            MutableComponent gainedMsg = Component.translatable("vtm.intel_gained")
                .withStyle(ChatFormatting.DARK_PURPLE);
            player.sendSystemMessage(gainedMsg, true);

            if (newNestIntel >= getSiegeThreshold()) {
                RandomSource random = player.level().random;
                if (random.nextFloat() < 0.40f) { 
                    triggerHunterSiege(player, level, chunkPos);
                    int resetIntel = getSiegeThreshold() / 2;
                    setChunkIntel(level, chunkPos, resetIntel);
                } 
            }
        }
    }

    @SubscribeEvent
    public static void onChunkDataLoad(ChunkDataEvent.Load event) {

        CompoundTag dataTag = event.getData();
        ChunkPos chunkPos = event.getChunk().getPos();
        
        if (dataTag.contains(CHUNK_INTEL_TAG)) {
            int intel = dataTag.getInt(CHUNK_INTEL_TAG);
            NEST_INTEL_CACHE.put(chunkPos, intel);
        } else {
            NEST_INTEL_CACHE.put(chunkPos, 0); 
        }
    }

    @SubscribeEvent
    public static void onChunkDataSave(ChunkDataEvent.Save event) {
        ChunkPos chunkPos = event.getChunk().getPos(); 
        CompoundTag dataTag = event.getData();

        if (NEST_INTEL_CACHE.containsKey(chunkPos)) {
            int intel = NEST_INTEL_CACHE.get(chunkPos);
            if (intel > 0) {
                dataTag.putInt(CHUNK_INTEL_TAG, intel);
            } else {
                dataTag.remove(CHUNK_INTEL_TAG);
            }
        }
    }
    
    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getChunk() instanceof LevelChunk) {
            NEST_INTEL_CACHE.remove(event.getChunk().getPos());
        }
    }

    public static int getChunkIntel(Level level, ChunkPos chunkPos) {
        return NEST_INTEL_CACHE.getOrDefault(chunkPos, 0);
    }

    private static void setChunkIntel(Level level, ChunkPos chunkPos, int intel) {
        if (level.isClientSide()) return;
        
        NEST_INTEL_CACHE.put(chunkPos, intel);
        
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        if (chunk != null) {
             chunk.setUnsaved(true); 
        }
    }

    public static void resetIntelForChunk(Level level, ChunkPos chunkPos) {
        setChunkIntel(level, chunkPos, 0);
    }
}