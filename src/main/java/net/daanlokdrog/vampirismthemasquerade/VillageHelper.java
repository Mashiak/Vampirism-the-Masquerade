package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import static net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy.*;
import static net.minecraft.world.entity.ai.village.poi.PoiTypes.HOME;

public class VillageHelper {

    public static int getBedCount(ServerLevel level, BlockPos center, int radius) {
        return (int) level.getPoiManager().getInRange(type -> type.is(HOME), center, radius, ANY).count();
    }

    public static int getVillagerCount(ServerLevel level, BlockPos center, int radius) {
        int occupiedBeds = (int) level.getPoiManager().getInRange(type -> type.is(HOME), center, radius, IS_OCCUPIED).count();
        int entityCount = level.getEntitiesOfClass(Villager.class, new AABB(center).inflate(radius)).size();
        
        return Math.max(occupiedBeds, entityCount);
    }

public static boolean hasVillageNearby(ServerLevel level, BlockPos center, int radius) {
        return getVillagerCount(level, center, radius) >= 7 || getBedCount(level, center, radius) >= 8;
    }
}