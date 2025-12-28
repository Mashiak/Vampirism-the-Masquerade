package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.player.tasks.TaskBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class MasqueradeTask {

    public static final DeferredRegister<MapCodec<? extends TaskUnlocker>> TASK_UNLOCKER =
            DeferredRegister.create(VampirismRegistries.Keys.TASK_UNLOCKER, VampirismTheMasqueradeMod.MODID);

    public static final DeferredRegister<MapCodec<? extends TaskReward>> TASK_REWARDS =
            DeferredRegister.create(VampirismRegistries.Keys.TASK_REWARD, VampirismTheMasqueradeMod.MODID);

    public static final DeferredRegister<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES =
            DeferredRegister.create(VampirismRegistries.Keys.TASK_REWARD_INSTANCE, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<MapCodec<? extends TaskUnlocker>, MapCodec<ExposureUnlocker>> EXPOSURE_UNLOCKER =
        TASK_UNLOCKER.register("exposure_unlocker", () -> ExposureUnlocker.CODEC);

    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<CoverExposureReward>> COVER_EXPOSURE_REWARD =
            TASK_REWARDS.register("cover_exposure", () -> CoverExposureReward.CODEC);

    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<CoverExposureReward>> COVER_EXPOSURE_REWARD_INSTANCE =
            TASK_REWARD_INSTANCES.register("cover_exposure", () -> CoverExposureReward.CODEC);


    public static final ResourceKey<Task> COVER_NOVICE = task("cover_novice");
    public static final ResourceKey<Task> COVER_ADEPT = task("cover_adept");
    public static final ResourceKey<Task> COVER_LORD = task("cover_lord");

    private static ResourceKey<Task> task(String path) {
        return ResourceKey.create(
                VampirismRegistries.Keys.TASK,
                ResourceLocation.fromNamespaceAndPath(VampirismTheMasqueradeMod.MODID, path)
        );
    }

    public static void createTasks(BootstrapContext<Task> context) {
        context.register(COVER_NOVICE, TaskBuilder.builder()
                .setTitle(COVER_NOVICE.location())
                .setReward(new CoverExposureReward())
                .unlockedBy(new ExposureUnlocker(100, ExposureUnlocker.Mode.NOVICE, false))
                .addRequirement(new ItemStack(Items.GOLD_INGOT, 10))
                .build());

        context.register(COVER_ADEPT, TaskBuilder.builder()
                .setTitle(COVER_ADEPT.location())
                .setReward(new CoverExposureReward())
                .unlockedBy(new ExposureUnlocker(100, ExposureUnlocker.Mode.ADEPT, false))
                .addRequirement(new ItemStack(Items.GOLD_INGOT, 32))
                .build());

        context.register(COVER_LORD, TaskBuilder.builder()
                .setTitle(COVER_LORD.location())
                .setReward(new CoverExposureReward())
                .unlockedBy(new ExposureUnlocker(100, ExposureUnlocker.Mode.LORD, false))
                .addRequirement(new ItemStack(Items.GOLD_INGOT, 64))
                .build());
    }

    public static void register(IEventBus modEventBus) {
        TASK_UNLOCKER.register(modEventBus);
        TASK_REWARDS.register(modEventBus);
        TASK_REWARD_INSTANCES.register(modEventBus);
    }
}

