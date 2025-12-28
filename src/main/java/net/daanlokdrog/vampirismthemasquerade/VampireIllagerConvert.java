package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.entity.VampirePillagerEntity;
import net.daanlokdrog.vampirismthemasquerade.entity.VampireVindicatorEntity;
import net.daanlokdrog.vampirismthemasquerade.entity.VampireEvokerEntity;
import net.daanlokdrog.vampirismthemasquerade.init.ModEntities;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

@EventBusSubscriber
public class VampireIllagerConvert {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Mob entity = event.getEntity() instanceof Mob ? (Mob) event.getEntity() : null;
        if (entity == null) return;
        if (event.getSpawnType() == MobSpawnType.EVENT) return;

        if (RANDOM.nextDouble() < 0.2) {
            Mob replacement = null;

            if (entity instanceof Pillager) {
                replacement = new VampirePillagerEntity(ModEntities.VAMPIRE_PILLAGER.get(), serverLevel);
                replacement.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
            } else if (entity instanceof Vindicator) {
                replacement = new VampireVindicatorEntity(ModEntities.VAMPIRE_VINDICATOR.get(), serverLevel);
                replacement.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
            } else if (entity instanceof Evoker) {
                replacement = new VampireEvokerEntity(ModEntities.VAMPIRE_EVOKER.get(), serverLevel);
            }

            if (replacement != null) {
                replacement.moveTo(event.getX(), event.getY(), event.getZ(),
                                   entity.getYRot(), entity.getXRot());
                replacement.setPersistenceRequired();
                replacement.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(replacement.blockPosition()),
                        MobSpawnType.EVENT, (SpawnGroupData) null);
                serverLevel.addFreshEntity(replacement);
                event.setSpawnCancelled(true);
            }
        }
    }
}
