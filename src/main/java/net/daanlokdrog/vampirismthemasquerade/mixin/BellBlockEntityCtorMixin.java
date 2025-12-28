package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BellBlockEntity.class)
public abstract class BellBlockEntityCtorMixin {

    private static final ResourceLocation THRALL_BELL_ID =
        ResourceLocation.parse("vampirism_the_masquerade:thrall_bell");

    private static final ResourceKey<net.minecraft.world.level.block.Block> THRALL_BELL_KEY =
        ResourceKey.create(Registries.BLOCK, THRALL_BELL_ID);

    private static final ResourceLocation THRALL_BELL_ENTITY_ID =
        ResourceLocation.parse("vampirism_the_masquerade:thrall_bell");

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntity;<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
        ),
        index = 0
    )
    private static BlockEntityType<?> replaceType(BlockEntityType<?> original, net.minecraft.core.BlockPos pos, BlockState state) {
        if (state.is(THRALL_BELL_KEY)) {
            BlockEntityType<?> thrallType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(THRALL_BELL_ENTITY_ID);
            if (thrallType != null) {
                return thrallType;
            }
        }
        return original;
    }
}
