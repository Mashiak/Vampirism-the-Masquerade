package net.daanlokdrog.vampirismthemasquerade.registry;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class MasqueradeRaiderTypes {
    public static final EnumProxy<Raid.RaiderType> VAMPIRE_PILLAGER =
            new EnumProxy<>(Raid.RaiderType.class,
                    getRaider(ModEntities.VAMPIRE_PILLAGER.get()),
                    new int[]{2, 3, 3, 3, 4, 4, 4, 5});

    public static final EnumProxy<Raid.RaiderType> VAMPIRE_VINDICATOR =
            new EnumProxy<>(Raid.RaiderType.class,
                    getRaider(ModEntities.VAMPIRE_VINDICATOR.get()),
                    new int[]{1, 1, 1, 2, 2, 3, 3, 4});

    public static final EnumProxy<Raid.RaiderType> VAMPIRE_EVOKER =
            new EnumProxy<>(Raid.RaiderType.class,
                    getRaider(ModEntities.VAMPIRE_EVOKER.get()),
                    new int[]{1, 1, 1, 1, 1, 1, 1, 1});

    private static Supplier<EntityType<?>> getRaider(EntityType<?> type) {
        return () -> type;
    }
}
