package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ExtendedCreature.class)
public abstract class ExtendedCreatureBloodReductionMixin {

    private boolean isMasqueradeAdvancedVampire(IVampire biter) {
        if (!(biter instanceof VampirePlayer)) {
            return false;
        }

        Player player = ((VampirePlayer) biter).getRepresentingPlayer();
        if (player == null) {
            return false;
        }

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int vampireLevel = atts.vampireLevel;

        return (vampireLevel >= 7);
    }

    @Inject(
        method = "onBite",
        at = @At(value = "RETURN", ordinal = 0),
        cancellable = true
    )
    private void vampirismthemasquerade$applyAdvancedVampireAnimalBloodReduction(
        IVampire biter,
        CallbackInfoReturnable<Integer> cir
    ) {
        ExtendedCreature self = (ExtendedCreature) (Object) this;

        boolean isAdvanced = isMasqueradeAdvancedVampire(biter);

        boolean isGenericCreature = !(self.getEntity() instanceof Villager);
        
        if (isAdvanced && isGenericCreature) {
            int finalAmt = cir.getReturnValue();

            int newAmt = Math.max(1, Math.round((float) finalAmt / 3f));

            cir.setReturnValue(newAmt);
        }
    }
}