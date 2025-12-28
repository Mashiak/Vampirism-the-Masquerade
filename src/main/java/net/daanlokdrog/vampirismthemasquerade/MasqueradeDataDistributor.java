package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.MasqueradeTasksProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber 
public class MasqueradeDataDistributor {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
            event.includeServer(),
            new MasqueradeTasksProvider(packOutput, lookupProvider, existingFileHelper)
        );
    }
}
