package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.daanlokdrog.vampirismthemasquerade.MinionTasks; 

@Mixin(VampireMinionEntity.class)
public abstract class VampireMinionEntityMixin {

    @Inject(method = "getAvailableTasks", at = @At("RETURN"), cancellable = true)
    private void vtm_injectCalmRumorsTask(CallbackInfoReturnable<List<IMinionTask<?, ?>>> cir) {
        List<IMinionTask<?, ?>> tasks = cir.getReturnValue();
        try {
            IMinionTask<?, ?> calmRumorsTask = MinionTasks.CALM_RUMORS.get();
            if (calmRumorsTask != null && !tasks.contains(calmRumorsTask)) {
                tasks.add(calmRumorsTask);
            }
        } catch (Exception e) {
        }
    }
}