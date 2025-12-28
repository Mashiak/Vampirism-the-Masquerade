package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class CommandRegistry {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GetPanicCommand.register(event.getDispatcher());
        SetPanicCommand.register(event.getDispatcher());
        CheckVillageCommand.register(event.getDispatcher());
       DumpVillageChunksCommand.register(event.getDispatcher());
        ForceMarkVillageCommand.register(event.getDispatcher());
        GetVillageCenterCommand.register(event.getDispatcher());
        ClearVillageCentersCommand.register(event.getDispatcher());
        DebugVillageExpandCommand.register(event.getDispatcher());
        UnbindDomainCommand.register(event.getDispatcher());
        GetGoodwillCommand.register(event.getDispatcher());
        AdvisorReviveCommand.register(event.getDispatcher());
        BeastCommand.register(event.getDispatcher());
    }
}