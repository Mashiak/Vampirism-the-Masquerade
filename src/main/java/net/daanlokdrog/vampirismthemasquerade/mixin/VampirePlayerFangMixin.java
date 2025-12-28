package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigHelper;

@Mixin(VampirePlayer.class)
public class VampirePlayerFangMixin {

    @ModifyConstant(
        method = "setFangType",
        constant = @Constant(intValue = 7)
    )
    private int replaceFangCount(int original) {
        return MasqueradeConfigHelper.getFangTypeCount();
    }
}