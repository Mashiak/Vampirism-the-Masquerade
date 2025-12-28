package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.client.renderer.entity.layers.VampirePlayerHeadLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigHelper;

@Mixin(VampirePlayerHeadLayer.class)
public class VampirePlayerHeadLayerMixin {
    @ModifyConstant(
        method = "<init>",
        constant = @Constant(intValue = 17)
    )
    private int replaceEyeOverlayCount(int original) {
        return MasqueradeConfigHelper.getEyeTypeCount();
    }

    @ModifyConstant(
        method = "<init>",
        constant = @Constant(intValue = 7)
    )
    private int replaceFangOverlayCount(int original) {
        return MasqueradeConfigHelper.getFangTypeCount();
    }
}
