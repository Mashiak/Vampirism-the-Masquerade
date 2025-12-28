package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.entity.player.TaskManager; // 导入 TaskManager
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class CoverExposureReward implements TaskReward, ITaskRewardInstance {

    public static final MapCodec<CoverExposureReward> CODEC =
        MapCodec.unit(new CoverExposureReward());

    private final Component description =
        Component.translatable("task_reward.masquerade.cover_exposure");

    @Override
    public void applyReward(@NotNull IFactionPlayer<?> factionPlayer) {
        PlayerVariables vars = factionPlayer.asEntity()
            .getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (factionPlayer.asEntity() instanceof ServerPlayer sp) {

            TaskManager taskManager = (TaskManager) factionPlayer.getTaskManager(); 
            MasqueradeTaskUtils.resetMasqueradeTasks(sp, taskManager); 

            var bossBar = HuntEventManager.getActiveBar(sp.getUUID());
            if (bossBar != null) {
                bossBar.removePlayer(sp);
                HuntEventManager.removeActiveBar(sp.getUUID());
            }

            if (vars != null) {
                vars.exposure = 0;
                vars.huntActive = false;
                vars.huntBegining = false;
                vars.spawnedHunters = 0;
                vars.huntKillCounter = 0;
                vars.huntTimes = 0;
                vars.huntValue = 0;
                vars.huntSpawnCooldown = 0;
                vars.markSyncDirty();
            }
        }

        factionPlayer.asEntity().displayClientMessage(
            Component.translatable("text.masquerade.cover_success"), true
        );
    }

    @Override
    public @NotNull ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public MapCodec<CoverExposureReward> codec() {
        return MasqueradeTaskRewards.COVER_EXPOSURE.get();
    }

    @Override
    public Component description() {
        return this.description;
    }
}
