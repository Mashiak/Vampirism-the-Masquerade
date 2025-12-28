package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskRadialScreen; 
import net.daanlokdrog.vampirismthemasquerade.MinionTasks;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SelectMinionTaskRadialScreen.Entry.class)
public abstract class MinionTaskEntryMixin {

    @Shadow(remap = false)
    public abstract IMinionTask<?, ?> getTask();

    @Inject(
        method = "getIconLoc", 
        at = @At("HEAD"), 
        cancellable = true,
        remap = false
    )
    private void vtm_injectCustomIconLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        
        IMinionTask<?, ?> task = this.getTask();

        if (task == MinionTasks.CALM_RUMORS.get()) {

            String namespace = VampirismTheMasqueradeMod.MODID;
            String path = "textures/gui/minion_tasks/calm_rumors.png";
            ResourceLocation customLoc = ResourceLocation.fromNamespaceAndPath(namespace, path);
            cir.setReturnValue(customLoc);
            cir.cancel();
        }
    }
}