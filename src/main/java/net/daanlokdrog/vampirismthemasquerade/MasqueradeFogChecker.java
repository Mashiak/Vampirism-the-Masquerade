package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.LevelFog;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class MasqueradeFogChecker {

public static boolean isInVampireFog(Entity entity) {
    LevelFog fog = LevelFog.get(entity.level());
    BlockPos pos = entity.blockPosition();
    return fog.isInsideArtificialVampireFogArea(pos) || Helper.isEntityInVampireBiome(entity);
}
}