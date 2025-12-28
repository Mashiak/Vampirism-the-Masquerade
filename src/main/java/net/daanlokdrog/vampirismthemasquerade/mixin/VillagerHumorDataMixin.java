package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.daanlokdrog.vampirismthemasquerade.data.HumorDataHolder;
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

import java.util.Random;

@Mixin(Villager.class)
public abstract class VillagerHumorDataMixin implements HumorDataHolder {

    @Unique
    private static final EntityDataAccessor<Integer> VTM_HUMOR =
            SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void vtm$defineHumorData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        Random rand = new Random();
        int humor = -1;
        if (rand.nextInt(100) < 20) {
            humor = rand.nextInt(4);
        }
        builder.define(VTM_HUMOR, humor);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void vtm$saveHumorData(CompoundTag tag, CallbackInfo ci) {
        int humor = vtm_getHumor();
        if (humor >= 0) {
            tag.putInt("VTM_Humor", humor);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void vtm$loadHumorData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("VTM_Humor")) {
            vtm_setHumor(tag.getInt("VTM_Humor"));
        }
    }

    @Override
    public int vtm_getHumor() {
        return ((Villager)(Object)this).getEntityData().get(VTM_HUMOR);
    }

    @Override
    public void vtm_setHumor(int value) {
        Villager self = (Villager)(Object)this;
        self.getEntityData().set(VTM_HUMOR, value);
        if (value >= 0) {
            self.getPersistentData().putInt("VTM_Humor", value);
        } else {
            self.getPersistentData().remove("VTM_Humor");
        }
    }
}
