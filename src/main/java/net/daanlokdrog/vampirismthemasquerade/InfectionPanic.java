package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.LevelFog;

public class InfectionPanic {
	
    private InfectionPanic() {
    }

    public static void addPanicOnConversion(IConvertedCreature<?> convertedCreature) {
        LivingEntity entity = convertedCreature.asEntity();
        if (entity.level().isClientSide() || !(entity.level() instanceof ServerLevel level)) {
            return;
        }

        BlockPos pos = entity.blockPosition();

        if (Helper.isEntityInVampireBiome(entity)) {
            return; 
        }
        
        boolean inFog = LevelFog.get(level).isInsideArtificialVampireFogArea(pos);
        
        if (inFog) {
            return;
        }

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        ChunkPos currentChunk = new ChunkPos(pos);
        ChunkPos centerKey = data.getCenterKeyForChunk(currentChunk);
        
        if (centerKey == null) {
            return; 
        }

        BlockPos villageCenter = data.getVillageCenter(centerKey);
        if (villageCenter == null) {
            return; 
        }

        double actualChange = PanicFactory.calculatePanicChange(
            level, 
            villageCenter, 
            15.0 
        );

        data.addPanicForChunk(centerKey, actualChange);
    }
}