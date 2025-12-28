package net.daanlokdrog.vampirismthemasquerade.datagen;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.registry.MasqueradeEntitySetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = VampirismTheMasqueradeMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MasqueradeDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            generator.addProvider(event.includeServer(),
                new MasqueradeEntitySetup.MasqueradeEntityTypeTagsProvider(packOutput, lookupProvider, helper));
        }
    }
}
