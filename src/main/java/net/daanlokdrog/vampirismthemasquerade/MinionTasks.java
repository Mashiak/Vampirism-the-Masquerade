package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod; 
import net.daanlokdrog.vampirismthemasquerade.CalmRumorsTask;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MinionTasks {
    
    public static final DeferredRegister<IMinionTask<?, ?>> MINION_TASKS = 
        DeferredRegister.create(VampirismRegistries.Keys.MINION_TASK, VampirismTheMasqueradeMod.MODID);


    public static final DeferredHolder<IMinionTask<?,?>, CalmRumorsTask<VampireMinionEntity.VampireMinionData>> CALM_RUMORS = 
        MINION_TASKS.register("calm_rumors", 
            () -> new CalmRumorsTask<>(
                data -> 60, 
                () -> VampireSkills.LORD_ROOT.get()
            )
        );

    public static void register(IEventBus bus) {
        MINION_TASKS.register(bus);
    }
}