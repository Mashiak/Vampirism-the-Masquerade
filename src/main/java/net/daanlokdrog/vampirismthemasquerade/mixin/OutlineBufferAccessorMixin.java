package net.daanlokdrog.vampirismthemasquerade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "de.teamlapen.vampirism.client.renderer.RenderHandler$OutlineBuffer")
public interface OutlineBufferAccessorMixin {
    @Accessor("teamR")
    void setTeamR(int r);

    @Accessor("teamG")
    void setTeamG(int g);

    @Accessor("teamB")
    void setTeamB(int b);

    @Accessor("teamA")
    void setTeamA(int a);
}
