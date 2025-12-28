package net.daanlokdrog.vampirismthemasquerade.util;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import net.minecraft.world.entity.player.Player;

public final class MasqueradeData {
    private MasqueradeData() {}

    public static double getExposure(Player player) {
        PlayerVariables vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        return vars != null ? vars.exposure : 0.0;
    }
}
