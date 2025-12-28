package net.daanlokdrog.vampirismthemasquerade.registry;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.init.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MasqueradeEntitySetup {

    public static class MasqueradeEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public MasqueradeEntityTypeTagsProvider(PackOutput output,
                                                CompletableFuture<HolderLookup.Provider> provider,
                                                ExistingFileHelper helper) {
            super(output, provider, VampirismTheMasqueradeMod.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.tag(EntityTypeTags.RAIDERS).add(
                    ModEntities.VAMPIRE_PILLAGER.get(),
                    ModEntities.VAMPIRE_VINDICATOR.get(),
                    ModEntities.VAMPIRE_EVOKER.get()
            );
        }
    }
}
