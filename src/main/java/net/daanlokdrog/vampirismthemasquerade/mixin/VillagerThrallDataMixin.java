package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.daanlokdrog.vampirismthemasquerade.data.ThrallDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerThrallDataMixin implements ThrallDataHolder {

    @Unique
    private static final EntityDataAccessor<Boolean> VTM_THRALL =
            SynchedEntityData.defineId(Villager.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void vtm$defineThrallData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(VTM_THRALL, Boolean.FALSE);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void vtm$saveThrallData(CompoundTag tag, CallbackInfo ci) {
        if (vtm_isThrall()) {
            tag.putBoolean("VTM_IsThrall", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void vtm$loadThrallData(CompoundTag tag, CallbackInfo ci) {
        if (tag.getBoolean("VTM_IsThrall")) {
            vtm_setThrall(true);
        }
    }

    @Override
    public boolean vtm_isThrall() {
        return ((Villager)(Object)this).getEntityData().get(VTM_THRALL);
    }

    @Override
    public void vtm_setThrall(boolean value) {
        Villager self = (Villager)(Object)this;
        self.getEntityData().set(VTM_THRALL, value);
        self.getPersistentData().putBoolean("VTM_IsThrall", value);
    }
}
