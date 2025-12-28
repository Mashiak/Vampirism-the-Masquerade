package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.world.LevelFog;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class VillageHunterSpawner {
    public static final String CENTER_KEY_X_TAG = "MasqueradeVillageCenterKeyX";
    public static final String CENTER_KEY_Z_TAG = "MasqueradeVillageCenterKeyZ";
    private static final String VG = "village_guard";

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (level.getGameTime() % 200 != 0 || level.isNight()) return;

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        
        for (ChunkPos centerKey : data.getAllVillageCenters()) {
            if (!level.hasChunk(centerKey.x, centerKey.z)) continue;

            BlockPos centerPos = data.getVillageCenter(centerKey);
            if (centerPos == null) continue;

            processVillageRecruitment(level, centerKey, centerPos, data);
        }
    }

    private static void processVillageRecruitment(ServerLevel level, ChunkPos key, BlockPos pos, MasqueradeVillageData data) {
        if (LevelFog.get(level).isInsideArtificialVampireFogArea(pos)) return;
        if (data.getPanicForChunk(key) < 70) return;

        int currentGuards = level.getEntitiesOfClass(BasicHunterEntity.class, new AABB(pos).inflate(64),
                e -> e.isAlive() && e.getTags().contains(VG)).size();
        
        data.setHunterCount(key, currentGuards);

        int maxGuards = Math.max(1, VillageHelper.getVillagerCount(level, pos, 64) / 3);
        if (currentGuards >= maxGuards) return;

        var random = level.getRandom();
        for (int i = 0; i < 3; i++) {
            if (random.nextFloat() < 0.5f) continue;

            BlockPos spawnPos = findSafePos(level, pos);
            if (spawnPos == null) continue;

            BasicHunterEntity hunter = ModEntities.HUNTER.get().spawn(level, spawnPos, MobSpawnType.EVENT);
            if (hunter != null) {
                hunter.getPersistentData().putInt(CENTER_KEY_X_TAG, key.x);
                hunter.getPersistentData().putInt(CENTER_KEY_Z_TAG, key.z);
                hunter.addTag(VG);
                data.incrementHunterCount(key);
                
                break; 
            }
        }
    }

    private static BlockPos findSafePos(ServerLevel level, BlockPos center) {
        var random = level.getRandom();
        for (int i = 0; i < 10; i++) {
            int x = center.getX() + random.nextInt(64) - 32;
            int z = center.getZ() + random.nextInt(64) - 32;
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));

            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                return pos;
            }
        }
        return null;
    }
}