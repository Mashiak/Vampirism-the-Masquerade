/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.daanlokdrog.vampirismthemasquerade.item.*;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.ThrallBellItem;

public class VampirismTheMasqueradeModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(VampirismTheMasqueradeMod.MODID);
	public static final DeferredItem<Item> TEMPLATE_1;
	public static final DeferredItem<Item> TEMPLATE_2;
	public static final DeferredItem<Item> COPPER_MASK_HELMET;
	public static final DeferredItem<Item> TERRITORY_CERTIFICATE;
	public static final DeferredItem<Item> VAMPIRE_EAR;
	public static final DeferredItem<Item> BLANK_LETTER;
	public static final DeferredItem<Item> NEWSPAPER_RACK;
	public static final DeferredItem<Item> NEWSPAPER_ITEM;
	public static final DeferredItem<Item> V_SPAWNER;
	public static final DeferredItem<Item> THRALL_TAG;
	public static final DeferredItem<Item> LILITH_TOTEM;
	public static final DeferredItem<Item> THRALL_BELL;
	public static final DeferredItem<Item> HANDKERCHIEF;
	public static final DeferredItem<Item> TEMPLATE_3;
	static {
		TEMPLATE_1 = REGISTRY.register("template_1", Template1Item::new);
		TEMPLATE_2 = REGISTRY.register("template_2", Template2Item::new);
		COPPER_MASK_HELMET = REGISTRY.register("copper_mask_helmet", CopperMaskItem.Helmet::new);
		TERRITORY_CERTIFICATE = REGISTRY.register("territory_certificate", TerritoryCertificateItem::new);
		VAMPIRE_EAR = REGISTRY.register("vampire_ear", VampireEarItem::new);
		BLANK_LETTER = REGISTRY.register("blank_letter", BlankLetterItem::new);
		NEWSPAPER_RACK = block(VampirismTheMasqueradeModBlocks.NEWSPAPER_RACK);
		NEWSPAPER_ITEM = REGISTRY.register("newspaper_item", NewspaperItemItem::new);
		V_SPAWNER = block(VampirismTheMasqueradeModBlocks.V_SPAWNER, new Item.Properties().fireResistant());
		THRALL_TAG = REGISTRY.register("thrall_tag", ThrallTagItem::new);
		LILITH_TOTEM = block(VampirismTheMasqueradeModBlocks.LILITH_TOTEM);
		THRALL_BELL = block(VampirismTheMasqueradeModBlocks.THRALL_BELL);
		HANDKERCHIEF = REGISTRY.register("handkerchief", HandkerchiefItem::new);
		TEMPLATE_3 = REGISTRY.register("template_3", Template3Item::new);
	}

	// Start of user code block custom items
	public static class ThrallBellItem extends BlockItem {
		public ThrallBellItem() {
			super(VampirismTheMasqueradeModBlocks.THRALL_BELL.get(), new Item.Properties().stacksTo(1).fireResistant());
		}
	}

	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}