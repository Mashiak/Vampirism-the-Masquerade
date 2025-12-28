package net.daanlokdrog.vampirismthemasquerade.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.ThrallBellBlockEntity;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModBlocks;

public class VampirismTheMasqueradeModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThrallBellBlockEntity>> THRALL_BELL_ENTITY =
        BLOCK_ENTITIES.register("thrall_bell",
            () -> BlockEntityType.Builder.of(
                ThrallBellBlockEntity::new,
                VampirismTheMasqueradeModBlocks.THRALL_BELL.get()
            ).build(null)
        );
}
