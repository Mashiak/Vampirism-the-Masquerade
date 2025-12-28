package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.client.gui.screens.VampirePlayerAppearanceScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigHelper;

@Mixin(VampirePlayerAppearanceScreen.class)
public class VampirePlayerAppearanceScreenMixin {
    @ModifyConstant(
        method = "init",
        constant = @Constant(intValue = 17)
    )
    private int replaceEyeListCount(int original) {
        return MasqueradeConfigHelper.getEyeTypeCount();
    }

    @ModifyConstant(
        method = "init",
        constant = @Constant(intValue = 7)
    )
    private int replaceFangListCount(int original) {
        return MasqueradeConfigHelper.getFangTypeCount();
    }
}
