package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import net.daanlokdrog.vampirismthemasquerade.util.NoObserverUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import java.util.Optional;

import java.util.Collections;
import java.util.stream.Collectors;

import static net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PLAYER_VARIABLES;

public class PanicEventEffects {

    private static void triggerMassMobilization(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 8.0F, 1.0F);

        var hunters = level.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(50),
                e -> e instanceof BasicHunterEntity || e instanceof AdvancedHunterEntity);

        Collections.shuffle(hunters);

        hunters.stream().limit(5).forEach(hunter -> {
            hunter.setTarget(null);
            hunter.getNavigation().stop();
            hunter.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof HunterInvestigateGoal);
            hunter.goalSelector.addGoal(1, new HunterInvestigateGoal(hunter, pos, 1.2D));
        });
    }

    public static void applyBitePanicEffect(Player player, LivingEntity bitten) {
        if (player.level().isClientSide || !(player.level() instanceof ServerLevel level) || bitten == null) return;

        double panic = Optional.ofNullable(MasqueradeVillageData.get(level))
                .map(d -> d.getPanicForChunk(d.getCenterKeyForChunk(new ChunkPos(player.blockPosition()))))
                .orElse(0.0);

        if (bitten instanceof Villager v && v.isSleeping() && panic > 70) {
            if (NoObserverUtil.isNoObserver(v) || !v.getPersistentData().getBoolean("VTM_IsObserverVillager")) return;

            PlayerVariables vars = player.getData(PLAYER_VARIABLES);
            vars.exposure = Math.min(100, vars.exposure + 20);
            vars.markSyncDirty();

            v.stopSleeping();
            v.setTarget(null);
            v.getNavigation().stop();
            v.playSound(SoundEvents.VILLAGER_HURT, 1.0F, 1.0F);

            level.sendParticles(ParticleTypes.ANGRY_VILLAGER, v.getX(), v.getEyeY(), v.getZ(), 5, 0.1, 0.1, 0.1, 0.05);

            if (level.random.nextFloat() < 0.5f) {
                level.playSound(null, v.blockPosition(), SoundEvents.SPLASH_POTION_THROW, v.getSoundSource(), 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                level.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 1, player.getZ(), 15, 0.5, 0.5, 0.5, 0.01);
                
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true));
                player.addEffect(new MobEffectInstance(ModEffects.GARLIC, 200, 0, false, true));
            }

            triggerMassMobilization(level, v.blockPosition());
        }
    }
}