package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public final class MasqueradeTaskTags {

    public static final TagKey<Task> MASQUERADE_REPEATABLE =
        TagKey.create(VampirismRegistries.Keys.TASK, ResourceLocation.fromNamespaceAndPath("vampirism", "masquerade_repeatable"));

    public static final TagKey<Task> IS_UNIQUE =
        TagKey.create(VampirismRegistries.Keys.TASK, ResourceLocation.parse("vampirism:is_unique"));

    public static final TagKey<Task> IS_VAMPIRE =
        TagKey.create(VampirismRegistries.Keys.TASK, ResourceLocation.parse("vampirism:is_vampire"));
}
