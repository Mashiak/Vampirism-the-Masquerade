package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@EventBusSubscriber
public class AdvisorDeathHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorDeathHandler.class);

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AdvancedVampireEntity) || entity.level().isClientSide) return;

        ServerLevel world = (ServerLevel) entity.level();
        CompoundTag nbt = entity.getPersistentData();

        if (!nbt.getBoolean("IsAdvisor")) return;

        ChunkPos pos = MasqueradeVillageData.get(world).getCenterKeyForChunk(entity.chunkPosition());
        String lordId = nbt.getString("VTM_LordUUID");

        if (pos == null || lordId.isEmpty()) return;

        try {
            ServerPlayer lord = world.getServer().getPlayerList().getPlayer(UUID.fromString(lordId));
            if (lord != null) {
                lord.sendSystemMessage(Component.translatable("vtm.advisor_status.defeated", entity.getDisplayName()).withStyle(ChatFormatting.RED));
            }
        } catch (Exception e) {
        }

        CompoundTag fullNBT = new CompoundTag();
        entity.save(fullNBT);

        DeadAdvisorStorage.get(world).addDeadAdvisor(pos, new StoredAdvisorData(fullNBT, world.getGameTime()));

        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), 
            ModSoundEvents.VAMPIRE_DEAD.get(), SoundSource.HOSTILE, 1.5f, 0.8f + world.random.nextFloat() * 0.2f);

        MasqueradeDomainData domain = MasqueradeDomainData.get(world);
        int newCount = domain.getCurrentAdvisorCount(pos) - 1;
        domain.setCurrentAdvisorCount(pos, newCount);
    }
}