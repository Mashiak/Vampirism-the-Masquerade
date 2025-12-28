package net.daanlokdrog.vampirismthemasquerade.init;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.entity.VampirePillagerEntity;
import net.daanlokdrog.vampirismthemasquerade.entity.VampireVindicatorEntity;
import net.daanlokdrog.vampirismthemasquerade.entity.VampireEvokerEntity;
import net.daanlokdrog.vampirismthemasquerade.entity.VampireIllusionerEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber
public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<VampirePillagerEntity>> VAMPIRE_PILLAGER =
            ENTITIES.register("vampire_pillager",
                    () -> EntityType.Builder.<VampirePillagerEntity>of(VampirePillagerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("vampire_pillager"));

    public static final DeferredHolder<EntityType<?>, EntityType<VampireVindicatorEntity>> VAMPIRE_VINDICATOR =
            ENTITIES.register("vampire_vindicator",
                    () -> EntityType.Builder.<VampireVindicatorEntity>of(VampireVindicatorEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("vampire_vindicator"));

    public static final DeferredHolder<EntityType<?>, EntityType<VampireEvokerEntity>> VAMPIRE_EVOKER =
            ENTITIES.register("vampire_evoker",
                    () -> EntityType.Builder.<VampireEvokerEntity>of(VampireEvokerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("vampire_evoker"));

    public static final DeferredHolder<EntityType<?>, EntityType<VampireIllusionerEntity>> VAMPIRE_ILLUSIONER =
            ENTITIES.register("vampire_illusioner",
                    () -> EntityType.Builder.<VampireIllusionerEntity>of(VampireIllusionerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("vampire_illusioner"));

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(VAMPIRE_PILLAGER.get(), VampirePillagerEntity.createAttributes().build());
        event.put(VAMPIRE_VINDICATOR.get(), VampireVindicatorEntity.createAttributes().build());
        event.put(VAMPIRE_EVOKER.get(), VampireEvokerEntity.createAttributes().build());
        event.put(VAMPIRE_ILLUSIONER.get(), VampireIllusionerEntity.createAttributes().build());
    }

    private ModEntities() {}
}
