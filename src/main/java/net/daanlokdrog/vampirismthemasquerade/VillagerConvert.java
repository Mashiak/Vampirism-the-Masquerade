package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.core.ModEntities;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

import java.util.Random;

import net.neoforged.fml.ModList;

@EventBusSubscriber
public class VillagerConvert {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (event.getEntity() instanceof Villager villager
            && !isMcaVillagerSoft(event.getEntity())) {

            double conversionChance = MasqueradeConfigConfiguration.CONVERTED_VILLAGER_SPAWN.get();

            if (RANDOM.nextDouble() < conversionChance) {
                EntityType<? extends ConvertedVillagerEntity> convertedType = ModEntities.VILLAGER_CONVERTED.get();
                ConvertedVillagerEntity convertedVillager = new ConvertedVillagerEntity(convertedType, serverLevel);
                convertedVillager.moveTo(event.getX(), event.getY(), event.getZ(), villager.getYRot(), villager.getXRot());
                convertedVillager.setPersistenceRequired();
                convertedVillager.setHealth(villager.getHealth());
                serverLevel.addFreshEntity(convertedVillager);
                event.setSpawnCancelled(true);
            }
        }

        if (ModList.get().isLoaded("vampirism_integrations")) {
            try {
                ClassLoader cl = VillagerConvert.class.getClassLoader();
                Class<?> mcaVillagerClass = Class.forName("net.conczin.mca.entity.VillagerEntityMCA", false, cl);
                Class<?> convertedMcaClass = Class.forName("de.teamlapen.vampirism_integrations.mca.ConvertedVillagerEntityMCA", false, cl);

                if (mcaVillagerClass.isInstance(event.getEntity())) {
                    double conversionChance = MasqueradeConfigConfiguration.CONVERTED_VILLAGER_SPAWN.get();

                    if (RANDOM.nextDouble() < conversionChance) {
                        Object convertedVillager = convertedMcaClass
                            .getConstructor(EntityType.class, ServerLevel.class)
                            .newInstance(null, serverLevel);

                        convertedMcaClass.getMethod("moveTo", double.class, double.class, double.class, float.class, float.class)
                            .invoke(convertedVillager, event.getX(), event.getY(), event.getZ(),
                                    event.getEntity().getYRot(), event.getEntity().getXRot());

                        convertedMcaClass.getMethod("setPersistenceRequired").invoke(convertedVillager);
                        convertedMcaClass.getMethod("setHealth", float.class)
                            .invoke(convertedVillager, ((net.minecraft.world.entity.LivingEntity) event.getEntity()).getHealth());

                        serverLevel.addFreshEntity((net.minecraft.world.entity.Entity) convertedVillager);
                        event.setSpawnCancelled(true);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static boolean isMcaVillagerSoft(Object entity) {
        if (!ModList.get().isLoaded("vampirism_integrations")) return false;
        try {
            Class<?> mcaVillagerClass = Class.forName("net.conczin.mca.entity.VillagerEntityMCA",
                                                      false, VillagerConvert.class.getClassLoader());
            return mcaVillagerClass.isInstance(entity);
        } catch (Throwable t) {
            return false;
        }
    }
}

