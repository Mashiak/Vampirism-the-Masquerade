package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.daanlokdrog.vampirismthemasquerade.VillageHelper;

public class PanicFactory {
    public static double calculatePanicChange(ServerLevel level, BlockPos villageCenter, double baseChange) {
    	
        int radius = 48;
        int villagerCount = VillageHelper.getVillagerCount(level, villageCenter, radius);
        if (villagerCount <= 0) villagerCount = 1;

        double adjustment = net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration.PANIC_CALCULATION_ADJUSTMENT_VALUE.get();

        return baseChange * (adjustment / Math.sqrt(villagerCount));
    }
}
