package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.player.TaskManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public final class MasqueradeTaskUtils {

    private MasqueradeTaskUtils() {}

    public static void resetMasqueradeTasks(ServerPlayer sp, TaskManager tm) {
        Registry<Task> registry = sp.level().registryAccess().registryOrThrow(VampirismRegistries.Keys.TASK);

        for (Holder<Task> holder : registry.getTagOrEmpty(MasqueradeTaskTags.MASQUERADE_REPEATABLE)) {
            holder.unwrapKey().ifPresent((ResourceKey<Task> key) -> tm.resetUniqueTask(key));
        }
    }
}

