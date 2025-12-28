package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MasqueradeTasksProvider extends TagsProvider<Task> {

    public static final TagKey<Task> MASQUERADE_REPEATABLE =
            TagKey.create(VampirismRegistries.Keys.TASK,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            VampirismTheMasqueradeMod.MODID, "masquerade_repeatable"));

    public MasqueradeTasksProvider(PackOutput output,
                                   CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   ExistingFileHelper existingFileHelper) {
        super(output,
              VampirismRegistries.Keys.TASK,
              lookupProvider,
              VampirismTheMasqueradeMod.MODID,
              existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        this.tag(ModTags.Tasks.IS_UNIQUE)
            .add(MasqueradeTask.COVER_NOVICE)
            .add(MasqueradeTask.COVER_ADEPT)
            .add(MasqueradeTask.COVER_LORD)
            .replace(true);

        this.tag(ModTags.Tasks.IS_VAMPIRE)
            .add(MasqueradeTask.COVER_NOVICE)
            .add(MasqueradeTask.COVER_ADEPT)
            .add(MasqueradeTask.COVER_LORD)
            .replace(true);

        this.tag(MASQUERADE_REPEATABLE)
            .add(MasqueradeTask.COVER_NOVICE)
            .add(MasqueradeTask.COVER_ADEPT)
            .add(MasqueradeTask.COVER_LORD)
            .replace(true);
    }
}
