/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;

import net.daanlokdrog.vampirismthemasquerade.block.VSpawnerBlock;
import net.daanlokdrog.vampirismthemasquerade.block.ThrallBellBlock;
import net.daanlokdrog.vampirismthemasquerade.block.NewspaperRackBlock;
import net.daanlokdrog.vampirismthemasquerade.block.LilithTotemBlock;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.ThrallBellBlockEntity;

public class VampirismTheMasqueradeModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(VampirismTheMasqueradeMod.MODID);
	public static final DeferredBlock<Block> NEWSPAPER_RACK;
	public static final DeferredBlock<Block> V_SPAWNER;
	public static final DeferredBlock<Block> LILITH_TOTEM;
	public static final DeferredBlock<Block> THRALL_BELL;
	static {
		NEWSPAPER_RACK = REGISTRY.register("newspaper_rack", NewspaperRackBlock::new);
		V_SPAWNER = REGISTRY.register("v_spawner", VSpawnerBlock::new);
		LILITH_TOTEM = REGISTRY.register("lilith_totem", LilithTotemBlock::new);
		THRALL_BELL = REGISTRY.register("thrall_bell", ThrallBellBlock::new);
	}

	// Start of user code block custom blocks
	public static class ThrallBellBlock extends net.minecraft.world.level.block.BellBlock {
		public ThrallBellBlock() {
			super(BlockBehaviour.Properties.of().strength(5.0F).sound(SoundType.ANVIL).noOcclusion().requiresCorrectToolForDrops());
		}
	}

	public static final net.neoforged.neoforge.registries.DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
			VampirismTheMasqueradeMod.MODID);
	public static final net.neoforged.neoforge.registries.DeferredHolder<BlockEntityType<?>, BlockEntityType<ThrallBellBlockEntity>> THRALL_BELL_ENTITY = BLOCK_ENTITIES.register("thrall_bell",
			() -> BlockEntityType.Builder.of(ThrallBellBlockEntity::new, VampirismTheMasqueradeModBlocks.THRALL_BELL.get()).build(null));
	// End of user code block custom blocks
}