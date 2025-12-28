package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;
import java.util.function.Predicate;

public class DomainFeedingRestriction {

    private static final Predicate<Mob> ENFORCER = m -> (m instanceof VampireBaseEntity || m.getType().is(EntityTypeTags.UNDEAD)) && m.isAlive();

    public static void checkAndPunishFeeding(Player feeder, LivingEntity victim) {
        if (feeder.level().isClientSide || !(feeder.level() instanceof ServerLevel level) || !Helper.isVampire(feeder)) return;

        ChunkPos chunk = new ChunkPos(feeder.blockPosition());
        ChunkPos center = MasqueradeVillageData.get(level).getCenterKeyForChunk(chunk);

        if (center == null) return;

        MasqueradeDomainData data = MasqueradeDomainData.get(level);
        if (!data.isDomainClaimed(center) || !DomainEdictStorage.get(level).isForbidOutsiderFeedingEnabled(center)) return;

        String owner = data.getLordUUID(center);
        if (owner != null && !owner.equals(feeder.getUUID().toString())) {
            punishFeeder(level, feeder, center);
        }
    }

    private static void punishFeeder(ServerLevel level, Player feeder, ChunkPos center) {
        level.getEntitiesOfClass(Mob.class, feeder.getBoundingBox().inflate(32.0), ENFORCER).stream()
            .filter(m -> m.getTarget() != feeder && m.hasLineOfSight(feeder))
            .forEach(m -> m.setTarget(feeder));

        level.playSound(null, feeder.getX(), feeder.getY(), feeder.getZ(), 
            ModSoundEvents.VAMPIRE_SERVICE.get(), SoundSource.PLAYERS, 1.0f, 1.0f + level.random.nextFloat() * 0.2f);

        feeder.sendSystemMessage(Component.translatable("vtm.edict.feeding_violation").withStyle(ChatFormatting.DARK_RED));

        String lordId = MasqueradeDomainData.get(level).getLordUUID(center);
        if (lordId != null) {
            Player lord = level.getPlayerByUUID(UUID.fromString(lordId));
            if (lord != null) {
                lord.sendSystemMessage(Component.translatable("vtm.edict.feeding_alert", feeder.getName()).withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
    }
}