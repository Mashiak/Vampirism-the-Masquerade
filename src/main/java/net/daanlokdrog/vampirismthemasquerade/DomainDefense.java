package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber
public class DomainDefense {

    @SubscribeEvent
    public static void onPlayerAttackedInDomain(LivingDamageEvent.Pre event) {
        LivingEntity damagedEntity = event.getEntity();
        DamageSource damageSource = event.getSource();
        Entity attacker = damageSource.getEntity();

        if (!(damagedEntity instanceof Player player) || player.level().isClientSide || !Helper.isVampire(player)) {
            return;
        }

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        if (atts.lordLevel <= 0) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) return;

        ChunkPos currentChunk = new ChunkPos(player.blockPosition());

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);
        DomainEdictStorage edictStorage = DomainEdictStorage.get(level);

        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);

        if (villageCenterKey == null || !domainData.isDomainClaimed(villageCenterKey)) {
            return;
        }

        if (!edictStorage.isMonsterProtectLordEnabled(villageCenterKey)) {
            return;
        }

        String lordUUIDString = domainData.getLordUUID(villageCenterKey);
        if (!player.getUUID().toString().equals(lordUUIDString)) {
            return;
        }

        if (attacker == null || attacker == player || !(attacker instanceof LivingEntity target)) {
            return;
        }

        if (attacker instanceof Player attackingPlayer) {
            if (attackingPlayer.getUUID().toString().equals(lordUUIDString)) {
                return;
            }
        }

        Predicate<Mob> enforcerPredicate = mob ->
            (
                mob instanceof BasicVampireEntity ||
                mob instanceof AdvancedVampireEntity ||
                mob instanceof VampireBaronEntity ||
                mob.getType().is(EntityTypeTags.UNDEAD)
            )
            && mob.isAlive()
            && mob != target
            && mob.hasLineOfSight(target);

        List<Mob> nearbyEnforcers = level.getEntitiesOfClass(
            Mob.class,
            player.getBoundingBox().inflate(32.0),
            enforcerPredicate
        );

        for (Mob enforcer : nearbyEnforcers) {
            if (enforcer.getTarget() != target) {
                enforcer.setTarget(target);
            }
        }
    }
}
