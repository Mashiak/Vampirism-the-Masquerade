package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock.CoffinPart;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@EventBusSubscriber
public class AdvisorRespawnManager {
    public static final int RESPAWN_DELAY_TICKS = 12000;
    private static final String CCX = "VTM_CoffinX", CCY = "VTM_CoffinY", CCZ = "VTM_CoffinZ";

@SubscribeEvent
public static void onPlayerTick(PlayerTickEvent.Post event) {
    if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;

    ServerLevel level = player.serverLevel();
    var villageData = MasqueradeVillageData.get(level);
    var domainData = MasqueradeDomainData.get(level);

    ChunkPos domainKey = villageData.getCenterKeyForChunk(new ChunkPos(player.blockPosition()));
    if (domainKey == null || !domainData.isDomainClaimed(domainKey)) return;
    if (domainData.getLordUUIDObject(domainKey).map(uuid -> !uuid.equals(player.getUUID())).orElse(true)) {
        return;
    }

    internalTryRespawnAdvisor(domainKey, player, level, domainData, DeadAdvisorStorage.get(level));
}

    private static void internalTryRespawnAdvisor(ChunkPos key, ServerPlayer lord, ServerLevel level, MasqueradeDomainData domainData, DeadAdvisorStorage storage) {
        var deadAdvisors = storage.getDAFD(key);
        if (deadAdvisors.isEmpty()) return;

        StoredAdvisorData data = deadAdvisors.get(0);
        if (level.getGameTime() < data.deathTick + RESPAWN_DELAY_TICKS) return;

        forceReviveSpecificAdvisor(key, lord, level, domainData, storage, data);
    }

    public static boolean forceReviveSpecificAdvisor(ChunkPos key, ServerPlayer lord, ServerLevel level, MasqueradeDomainData domainData, DeadAdvisorStorage storage, StoredAdvisorData storedData) {
        CompoundTag nbt = storedData.advisorNBT.copy();
        cleanAdvisorNBT(nbt);

        EntityType<?> type = ResourceLocation.tryParse(nbt.getString("id")) != null 
                ? BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(nbt.getString("id"))) : null;

        if (type == null) {
            storage.removeDeadAdvisor(key, 0);
            return false;
        }

        Entity entity = type.create(level);
        if (entity == null) {
            storage.removeDeadAdvisor(key, 0);
            return false;
        }

        entity.load(nbt);
        BlockPos spawnPos = getPreferredSpawnPos(nbt, level).orElse(lord.blockPosition().above());
        
        nbt.remove(CCX); nbt.remove(CCY); nbt.remove(CCZ);

        if (entity instanceof AdvancedVampireEntity advisor) {
            advisor.setHealth(advisor.getMaxHealth());
            advisor.removeAllEffects();
            advisor.clearFire();
            advisor.setAirSupply(advisor.getMaxAirSupply());

            var persistent = advisor.getPersistentData();
            persistent.putBoolean("IsAdvisor", true);
            persistent.putUUID("VTM_LordUUID", lord.getUUID()); 
        }

        entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, entity.getYRot(), entity.getXRot());

        if (level.addFreshEntity(entity)) {
            storage.removeDeadAdvisor(key, 0);
            domainData.setCurrentAdvisorCount(key, domainData.getCurrentAdvisorCount(key) + 1);

            lord.sendSystemMessage(Component.translatable("vtm.advisor_status.revived", entity.getName()).withStyle(ChatFormatting.DARK_PURPLE));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSoundEvents.VAMPIRE_REVIVED.get(), SoundSource.PLAYERS, 1.2f, 1.0f + level.random.nextFloat() * 0.2f);
            return true;
        }
        return false;
    }

    public static void cleanAdvisorNBT(CompoundTag nbt) {
        String[] toRemove = {"Health", "ActiveEffects", "IsDead", "FallFlying", "HurtTime", "DeathTime", "Air", "Motion"};
        for (String s : toRemove) nbt.remove(s);
        nbt.putShort("Fire", (short) 0);
    }

    private static Optional<BlockPos> getPreferredSpawnPos(CompoundTag nbt, ServerLevel level) {
        if (!nbt.contains(CCX)) return Optional.empty();
        
        BlockPos footPos = new BlockPos(nbt.getInt(CCX), nbt.getInt(CCY), nbt.getInt(CCZ));
        BlockState state = level.getBlockState(footPos);
        
        if (state.getBlock() instanceof CoffinBlock && !isCoffinOccupied(state, footPos, level)) {
            BlockPos headPos = getCoffinHeadPos(footPos, state);
            if (headPos != null && level.getBlockState(headPos).getBlock() instanceof CoffinBlock) {
                return Optional.of(headPos.above());
            }
        }
        return Optional.empty();
    }

    private static boolean isCoffinOccupied(BlockState state, BlockPos pos, Level level) {
        BlockPos headPos = getCoffinHeadPos(pos, state);
        return headPos == null || level.getBlockState(headPos).getValue(BedBlock.OCCUPIED);
    }

    private static BlockPos getCoffinHeadPos(BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof CoffinBlock)) return null;
        if (state.getValue(CoffinBlock.PART) == CoffinPart.HEAD) return pos;

        return state.getValue(CoffinBlock.VERTICAL) ? pos.above() : pos.relative(state.getValue(HORIZONTAL_FACING));
    }
}