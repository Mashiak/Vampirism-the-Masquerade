package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension; 
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.daanlokdrog.vampirismthemasquerade.DialoguePacket;
import net.daanlokdrog.vampirismthemasquerade.DialogueUpdatePacket; 
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.NegotiationMenu; 
import net.daanlokdrog.vampirismthemasquerade.NegotiationScreen;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModItems;

public class ModEventBusSubscriber {

    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(Registries.MENU, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<NegotiationMenu>> NEGOTIATION_MENU = 
        MENUS.register("negotiation_menu", () -> IMenuTypeExtension.create(NegotiationMenu::new));


    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VampirismTheMasqueradeMod.MODID);

        registrar.playToServer(
            DialoguePacket.TYPE,
            DialoguePacket.STREAM_CODEC,
            (payload, context) -> DialoguePacket.handle(payload, context)
        );

        registrar.playToClient(
            DialogueUpdatePacket.TYPE,
            DialogueUpdatePacket.STREAM_CODEC,
            (payload, context) -> DialogueUpdatePacket.handle(payload, context)
        );
    }
    
    @EventBusSubscriber(modid = VampirismTheMasqueradeMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModEventBusSubscriber.NEGOTIATION_MENU.get(), NegotiationScreen::new);
        }

    }
}
