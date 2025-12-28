package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigHelper;

@Mixin(VampirePlayer.class)
public class VampirePlayerEyeMixin {

    @ModifyConstant(
        method = "setEyeType",
        constant = @Constant(intValue = 17)
    )
    private int replaceEyeCount(int original) {
        return MasqueradeConfigHelper.getEyeTypeCount();
    }
}