package net.daanlokdrog.vampirismthemasquerade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import de.teamlapen.vampirism.REFERENCE;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

@Mixin(REFERENCE.class)
public class ReferenceMixin {

    @Shadow @Mutable public static int EYE_TYPE_COUNT;
    @Shadow @Mutable public static int FANG_TYPE_COUNT;

    static {
        EYE_TYPE_COUNT = MasqueradeConfigConfiguration.EYE_TYPE.get();
        FANG_TYPE_COUNT = MasqueradeConfigConfiguration.FANG_TYPE.get();
    }
}
