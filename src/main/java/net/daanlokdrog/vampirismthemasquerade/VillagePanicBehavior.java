package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModMobEffects;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.util.NoObserverUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;

@EventBusSubscriber
public class VillagePanicBehavior {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        int interval;
        try {
            interval = (int) (MasqueradeConfigConfiguration.PANIC_BEHAVIOR_INTERVAL.get() * 20);
        } catch (Exception e) {
            interval = 200;
        }
        if (interval <= 0) interval = 200;

        if (event.getServer().getTickCount() % interval != 0) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.hasEffect(MobEffects.INVISIBILITY) || !Helper.isVampire(player)) continue;
            if (player.hasEffect(VampirismTheMasqueradeModMobEffects.DEVIL_OF_THE_WORLD)) continue;

            var vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
            if (vars == null) continue;

            ServerLevel world = player.serverLevel();
            MasqueradeVillageData data = MasqueradeVillageData.get(world);
            ChunkPos key = data.getCenterKeyForChunk(new ChunkPos(player.blockPosition()));
            if (key == null) continue;

            double panic = data.getPanicForChunk(key);
            if (panic < 20.0) continue;

            if (vars.exposure > 10 && vars.exposure <= 80 && world.random.nextFloat() < 0.15f) {
                List<Villager> observers = getNearbyObservers(world, player, 8.0);
                if (!observers.isEmpty()) {
                    Villager v = observers.get(world.random.nextInt(observers.size()));
                    if (world.random.nextBoolean()) {
                        player.addEffect(new MobEffectInstance(ModEffects.GARLIC, 120, 0, false, true));
                        player.sendSystemMessage(Component.translatable("vampirism_the_masquerade.panic_throw_garlic"));
                    } else {
                        throwEgg(world, v, player);
                    }
                    flee(v, player.position());
                    continue;
                }
            }

            float chance = panic >= 40.0 ? 0.10f : 0.05f;
            if (world.random.nextFloat() < chance) {
                List<Villager> observers = getNearbyObservers(world, player, 12.0);
                if (!observers.isEmpty()) {
                    observers.get(world.random.nextInt(observers.size())).getNavigation().moveTo(player, 0.6);
                }
            }
        }
    }

    private static List<Villager> getNearbyObservers(ServerLevel level, ServerPlayer player, double range) {
        return level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(range), v -> 
            !NoObserverUtil.isNoObserver(v) && v.getPersistentData().getBoolean("VTM_IsObserverVillager")
        );
    }

    private static void throwEgg(ServerLevel level, Villager v, ServerPlayer target) {
        ThrownEgg egg = new ThrownEgg(level, v);
        egg.setPos(v.getX(), v.getEyeY(), v.getZ());
        Vec3 dir = target.getEyePosition().subtract(v.getEyePosition()).normalize();
        egg.shoot(dir.x, dir.y, dir.z, 0.7f, 1.0f);
        level.addFreshEntity(egg);
    }

    private static void flee(Villager v, Vec3 dangerPos) {
        Vec3 fleeDir = v.position().subtract(dangerPos).normalize();
        Vec3 target = v.position().add(fleeDir.scale(8.0));
        v.getNavigation().moveTo(target.x, target.y, target.z, 1.2);
    }
}