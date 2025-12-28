package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.UUID;

@EventBusSubscriber
public class UndeadPacifist {

    @SubscribeEvent
    public static void onUndeadDamagedByLord(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Mob mob) || !mob.getType().is(EntityTypeTags.UNDEAD)) return;
        if (mob.level().isClientSide || !(mob.level() instanceof ServerLevel level)) return;

        if (!(event.getSource().getEntity() instanceof Player lord) || !Helper.isVampire(lord)) return;

        ChunkPos pos = new ChunkPos(mob.blockPosition());
        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);

        ChunkPos center = villageData.getCenterKeyForChunk(pos);

        if (center == null || !domainData.isDomainClaimed(center)) return;

        String ownerId = domainData.getLordUUID(center);
        if (ownerId == null || !ownerId.equals(lord.getUUID().toString())) return;

        if (mob.getTarget() != null) mob.setTarget(null);
        mob.setLastHurtByMob(null);
        mob.setAggressive(false);
    }
}