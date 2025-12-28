package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModBlockEntities;
import net.daanlokdrog.vampirismthemasquerade.ThrallBellRenderer;

@EventBusSubscriber(value = Dist.CLIENT)
public class VtMClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(
            VampirismTheMasqueradeModBlockEntities.THRALL_BELL_ENTITY.get(),
            ThrallBellRenderer::new
        );
    }
}
