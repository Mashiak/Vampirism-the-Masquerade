package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.util.Helper;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class AttackPanic {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Villager villager) || villager.level().isClientSide) return;

        if (!(event.getSource().getEntity() instanceof Player player) || !Helper.isVampire(player)) return;

        ServerLevel world = (ServerLevel) villager.level();
        BlockPos pos = villager.blockPosition();
        MasqueradeVillageData data = MasqueradeVillageData.get(world);
        
        ChunkPos chunk = new ChunkPos(pos);
        ChunkPos centerKey = data.getCenterKeyForChunk(chunk);
        if (centerKey == null) return;

        BlockPos center = data.getVillageCenter(centerKey);
        if (center == null) return;

        float dmg = event.getNewDamage();
        if (dmg <= 0) return;

        double cap = 20.0;
        try {
            cap = MasqueradeConfigConfiguration.MAX_DAMAGE_FOR_PANIC.get().doubleValue();
        } catch (Exception ignored) {}

        double change = PanicFactory.calculatePanicChange(world, center, Math.min(dmg, cap));
        data.addPanicForChunk(chunk, change);
    }
}