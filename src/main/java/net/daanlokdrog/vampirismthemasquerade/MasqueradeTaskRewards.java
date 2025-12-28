package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasqueradeTaskRewards {

    public static final DeferredRegister<MapCodec<? extends TaskReward>> TASK_REWARDS =
        DeferredRegister.create(VampirismRegistries.Keys.TASK_REWARD, "vampirism_the_masquerade");

    public static final DeferredRegister<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES =
        DeferredRegister.create(VampirismRegistries.Keys.TASK_REWARD_INSTANCE, "vampirism_the_masquerade");

    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<CoverExposureReward>> COVER_EXPOSURE =
        TASK_REWARDS.register("cover_exposure", () -> CoverExposureReward.CODEC);

    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<CoverExposureReward>> COVER_EXPOSURE_INSTANCE =
        TASK_REWARD_INSTANCES.register("cover_exposure", () -> CoverExposureReward.CODEC);

    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<DonationReward>> DONATION_REWARD =
            TASK_REWARDS.register("donation_reward", () -> DonationReward.CODEC);

    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<DonationReward>> DONATION_REWARD_INSTANCE =
            TASK_REWARD_INSTANCES.register("donation_reward", () -> DonationReward.CODEC);

    public static void register(IEventBus modEventBus) {
        TASK_REWARDS.register(modEventBus);
        TASK_REWARD_INSTANCES.register(modEventBus);
    }
}
