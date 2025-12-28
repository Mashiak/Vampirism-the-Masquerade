package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.core.Holder;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import static net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PLAYER_VARIABLES;
import static net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration.HUNT_REINFORCEMENT_FACTOR;
import static de.teamlapen.vampirism.core.ModEntities.HUNTER;
import static de.teamlapen.vampirism.core.ModEntities.ADVANCED_HUNTER;

@EventBusSubscriber
public class HuntTickHandler {

    private static final String TAG_SPAWNED = "masquerade_spawned";
    private static final String TARGET_UUID = "masquerade_target_uuid";
    private static final ResourceLocation ADV_WRATH = ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "hunters_wrath");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp) || !Helper.isVampire(sp)) return;

        var vars = sp.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars.huntSpawnCooldown > 0) vars.huntSpawnCooldown--;

        if (vars.exposure >= 100 && !vars.huntActive) HuntEventManager.startHunt(sp);

        if (vars.huntActive) {
            HuntEventManager.tick(sp);
            if (vars.huntBegining && vars.huntSpawnCooldown == 0 && sp.tickCount % 20 == 0) {
                handleHunterSpawning(sp, vars);
            }
            if (sp.tickCount % 20 == 0) steerHuntersTowards(sp);
        }
    }

private static void handleHunterSpawning(ServerPlayer sp, PlayerVariables vars) {
    ServerLevel level = sp.serverLevel();
    var box = sp.getBoundingBox().inflate(128);
    int basicCount = level.getEntitiesOfClass(BasicHunterEntity.class, box).size();
    int advCount = level.getEntitiesOfClass(AdvancedHunterEntity.class, box).size();

    if (vars.huntKillCounter <= 10) {
        if (basicCount == 0) {
            spawnHunters(sp, 2, false, false);
            vars.huntSpawnCooldown = 200;
        }
    } else if (basicCount < 8) {
        int toSpawn = Math.min(4 + level.random.nextInt(3), 8 - basicCount);
        boolean spawnAdv = advCount == 0 && level.random.nextFloat() < 0.5f;
        
        spawnHunters(sp, toSpawn, true, spawnAdv);
        vars.huntSpawnCooldown = 600 + level.random.nextInt(401);
        grantAdvancement(sp);
    }
    vars.markSyncDirty();
}

public static void spawnHunters(ServerPlayer player, int basicCount, boolean playHorn, boolean spawnAdvanced) {
    ServerLevel level = player.serverLevel();
    PlayerVariables vars = player.getData(PLAYER_VARIABLES);
    double rf = HUNT_REINFORCEMENT_FACTOR.get();
    int spawned = 0;
    for (int i = 0; i < basicCount; i++) {
    if (doSpawn(level, player, HUNTER.get(), vars, rf)) spawned++;
    }
    if (spawnAdvanced && doSpawn(level, player, ADVANCED_HUNTER.get(), vars, rf)) {
    spawned++;
    }
    if (spawned > 0) {
        if (playHorn) {
            level.playSound(null, player.blockPosition(), SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 6.0F, 1.0F);
        }
        vars.spawnedHunters += spawned;
        vars.markSyncDirty();
    }
}

private static boolean doSpawn(ServerLevel level, ServerPlayer player, EntityType<? extends Mob> type, 
                               PlayerVariables vars, double rf) {
    BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 
            player.blockPosition().offset(level.random.nextInt(41) - 20, 0, level.random.nextInt(41) - 20));
    
    Mob hunter = type.spawn(level, pos, MobSpawnType.MOB_SUMMONED);
    if (hunter != null) {
        hunter.getPersistentData().putBoolean(TAG_SPAWNED, true);
        hunter.getPersistentData().putUUID(TARGET_UUID, player.getUUID());
        
        scaleAttribute(hunter, Attributes.MAX_HEALTH, 1.0 + (vars.huntTimes * rf), true);
        scaleAttribute(hunter, Attributes.ATTACK_DAMAGE, 1.0 + (vars.huntTimes * rf * 0.5), false);
        return true;
    }
    return false;
}

private static void scaleAttribute(Mob mob, Holder<Attribute> attr, double factor, boolean updateHealth) {
    var instance = mob.getAttribute(attr);
    if (instance != null) {
        double newValue = instance.getBaseValue() * factor;
        instance.setBaseValue(newValue);
        if (updateHealth) mob.setHealth((float) newValue);
    }
}

    private static void steerHuntersTowards(ServerPlayer player) {
        player.serverLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(48), 
            e -> (e instanceof BasicHunterEntity || e instanceof AdvancedHunterEntity) && e.getPersistentData().getBoolean(TAG_SPAWNED))
            .forEach(hunter -> {
                if (player.getUUID().equals(hunter.getPersistentData().getUUID(TARGET_UUID)) && hunter.distanceTo(player) > 8) {
                    hunter.getNavigation().moveTo(player, hunter instanceof AdvancedHunterEntity ? 1.25D : 1.2D);
                }
            });
    }

    private static void grantAdvancement(ServerPlayer sp) {
        var adv = sp.server.getAdvancements().get(ADV_WRATH);
        if (adv != null) {
            var progress = sp.getAdvancements().getOrStartProgress(adv);
            if (!progress.isDone()) progress.getRemainingCriteria().forEach(c -> sp.getAdvancements().award(adv, c));
        }
    }
}