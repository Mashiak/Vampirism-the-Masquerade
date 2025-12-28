/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;

@EventBusSubscriber
public class VampirismTheMasqueradeModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VampirismTheMasqueradeMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
			tabData.accept(VampirismTheMasqueradeModItems.COPPER_MASK_HELMET.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			tabData.accept(VampirismTheMasqueradeModBlocks.NEWSPAPER_RACK.get().asItem());
			tabData.accept(VampirismTheMasqueradeModBlocks.V_SPAWNER.get().asItem());
			tabData.accept(VampirismTheMasqueradeModBlocks.LILITH_TOTEM.get().asItem());
		} else if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(VampirismTheMasqueradeModItems.THRALL_TAG.get());
			tabData.accept(VampirismTheMasqueradeModItems.HANDKERCHIEF.get());
		}
	}
}