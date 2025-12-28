package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.world.LevelFog;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModMobEffects;
import net.daanlokdrog.vampirismthemasquerade.util.NoObserverUtil;

@EventBusSubscriber
public class PlayerTickHandler {

    private static final TagKey<Item> MASQUERADE_MASK =
        ItemTags.create(ResourceLocation.parse("vampirism_the_masquerade:mask"));

    private static final String VO = "VTM_IsObserverVillager";
    private static final String HO = "VTM_IsObserverHunter";

@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (!(player instanceof ServerPlayer serverPlayer)) return;

    ServerLevel serverLevel = serverPlayer.serverLevel();
    if (!Helper.isVampire(player)) return;

    VampirismTheMasqueradeModVariables.PlayerVariables vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
    if (vars == null) return;

    int decayIntervalTicks = MasqueradeConfigConfiguration.NATURAL_DECAY_INTERVAL_SECONDS.get().intValue() * 20;
    int hunterIntervalTicks = MasqueradeConfigConfiguration.HUNTER_INCREASE_INTERVAL_SECONDS.get().intValue() * 20;
    double villagerRadius = MasqueradeConfigConfiguration.VILLAGER_OBSERVE_RADIUS.get();
    double hunterRadius = MasqueradeConfigConfiguration.HUNTER_OBSERVE_RADIUS.get();
    int invisibilityDecay = MasqueradeConfigConfiguration.INVISIBILITY_DECAY_RATE.get().intValue();

    boolean inVampireForest = Helper.isEntityInVampireBiome(player);
    boolean inVampireFog = LevelFog.get(serverLevel).isInsideArtificialVampireFogArea(player.blockPosition());
    boolean shouldIncreasePanic = !(inVampireForest || inVampireFog);

    VampirePlayer vPlayer = VampirePlayer.get(player);
    boolean vampInvisible = false;
    if (vPlayer != null) {
        VampirePlayerSpecialAttributes attrs = vPlayer.getSpecialAttributes();
        vampInvisible = attrs != null && attrs.invisible;
    }
    boolean vanillaInvisible = player.isInvisible() || player.hasEffect(MobEffects.INVISIBILITY);
    boolean isInvisible = vanillaInvisible || vampInvisible;

    if (isInvisible) {
        if (player.tickCount % decayIntervalTicks == 0) {
            int fastDecay = invisibilityDecay * 2;
            vars.exposure = Math.max(0, vars.exposure - fastDecay);
            vars.markSyncDirty();
        }
        return;
    }

    double maxRadius = Math.max(villagerRadius, hunterRadius);
    List<net.minecraft.world.entity.LivingEntity> nearby = player.level().getEntitiesOfClass(
        net.minecraft.world.entity.LivingEntity.class,
        player.getBoundingBox().inflate(maxRadius)
    );

    boolean villagerObserved = false;
    boolean hunterObserved = false;

    for (net.minecraft.world.entity.LivingEntity e : nearby) {
        if (NoObserverUtil.isNoObserver(e)) continue;

        double distSq = e.distanceToSqr(player);

        if (!villagerObserved
            && distSq <= villagerRadius * villagerRadius
            && e.getPersistentData().getBoolean(VO)
            && !e.isSleeping()
            && e.hasLineOfSight(player)) {
            villagerObserved = true;
        }

        if (!hunterObserved
            && distSq <= hunterRadius * hunterRadius
            && e.getPersistentData().getBoolean(HO)
            && e.hasLineOfSight(player)) {
            hunterObserved = true;
        }

        if (villagerObserved && hunterObserved) break;
    }
    if (player.tickCount % decayIntervalTicks == 0) {
        if (player.hasEffect(VampirismTheMasqueradeModMobEffects.DEVIL_OF_THE_WORLD)) {
            vars.markSyncDirty();
            return;
        }

        ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        boolean hasMask = head.getItem().builtInRegistryHolder().is(MASQUERADE_MASK);
        boolean disguised = vPlayer != null && vPlayer.isDisguised();
        boolean hasMasqueradeProtection = hasMask || disguised;

        if (BloodOverlayMarker.isBloodied(serverPlayer)) {
            hasMasqueradeProtection = false;
        }

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int vampireLevel = atts != null ? atts.vampireLevel : 0;
        int exposureCap = (vampireLevel < 7) ? 50 : 100;

        boolean isCurrentlyObserved = villagerObserved || hunterObserved;

        if (!hasMasqueradeProtection) {
            if (isCurrentlyObserved && player.tickCount % hunterIntervalTicks == 0) {
                vars.exposure = Math.min(exposureCap, vars.exposure + 1);
                if (shouldIncreasePanic) {
                    increaseVillagePanic(serverLevel, BlockPos.containing(player.getPosition(0)), 0.1);
                }
            }
        } else {
            if (hunterObserved && vars.exposure > 40 && player.tickCount % hunterIntervalTicks == 0) {
                vars.exposure = Math.min(exposureCap, vars.exposure + 1);
                if (shouldIncreasePanic) {
                    increaseVillagePanic(serverLevel, BlockPos.containing(player.getPosition(0)), 0.1);
                }
            }
        }

        if (!isCurrentlyObserved) {
            AdvancementHolder adv = serverPlayer.server.getAdvancements()
                .get(ResourceLocation.parse("vampirism_the_masquerade:keep_the_masquerade"));
            boolean keepFloor = true;
            if (adv != null) {
                AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
                keepFloor = !ap.isDone();
            }

            if (keepFloor) {
                vars.exposure = Math.max(40, vars.exposure - 1);
                if (vars.exposure == 40 && adv != null) {
                    AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria()) {
                            serverPlayer.getAdvancements().award(adv, criteria);
                        }
                    }
                }
            } else {
                vars.exposure = Math.max(0, vars.exposure - 1);
            }
        }

        vars.markSyncDirty();
    }
}

    private static void increaseVillagePanic(ServerLevel level, BlockPos pos, double delta) {
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        ChunkPos cp = new ChunkPos(pos);
        ChunkPos centerKey = data.getCenterKeyForChunk(cp);

        if (centerKey != null) {
            data.addPanicForChunk(centerKey, delta);
        }
    }
}
