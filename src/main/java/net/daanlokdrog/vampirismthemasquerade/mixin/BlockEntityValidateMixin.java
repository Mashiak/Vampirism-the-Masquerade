package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityValidateMixin {

    private static final ResourceLocation THRALL_BELL_ID =
        ResourceLocation.parse("vampirism_the_masquerade:thrall_bell");

    private static final ResourceKey<net.minecraft.world.level.block.Block> THRALL_BELL_KEY =
        ResourceKey.create(Registries.BLOCK, THRALL_BELL_ID);

    @Inject(method = "validateBlockState", at = @At("HEAD"), cancellable = true)
    private void allowCustomBell(BlockState state, CallbackInfo ci) {
        if (state.is(THRALL_BELL_KEY)) {
            ci.cancel();
        }
    }
}
