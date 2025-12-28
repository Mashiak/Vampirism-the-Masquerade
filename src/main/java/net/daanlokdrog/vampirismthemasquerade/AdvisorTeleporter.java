package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock.CoffinPart;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@EventBusSubscriber
public class AdvisorTeleporter {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide || !(event.getEntity() instanceof AdvancedVampireEntity advisor)) return;
        
        if (advisor.tickCount % 100 != 0) return;

        CompoundTag tag = advisor.getPersistentData();
        if (!tag.getBoolean("IsAdvisor") || !tag.contains("VTM_CenterKeyX")) return;

        ServerLevel world = (ServerLevel) advisor.level();
        Optional<BlockPos> homePos = getHome(tag, world);
        BlockPos target = homePos.orElseGet(() -> getCenter(tag, world));

        if (!world.isLoaded(target)) return;

        double distSq = advisor.blockPosition().distSqr(target);
        double threshold = homePos.isPresent() ? 4096.0 : 2500.0; 

        if (distSq > threshold) {
            if (advisor.isSleeping()) return;

            performTeleport(advisor, target, world);
        }
    }

    private static void performTeleport(AdvancedVampireEntity advisor, BlockPos target, ServerLevel world) {
        double tx = target.getX() + 0.5;
        double ty = target.getY();
        double tz = target.getZ() + 0.5;
        Component msg = advisor.getDisplayName().copy().withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                .append(Component.translatable("vtm.advisor.teleported").withStyle(ChatFormatting.YELLOW));
        advisor.teleportTo(tx, ty, tz);
        world.playSound(null, target, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        world.getEntitiesOfClass(ServerPlayer.class, advisor.getBoundingBox().inflate(24.0))
                .forEach(p -> p.sendSystemMessage(msg));
    }

    private static Optional<BlockPos> getHome(CompoundTag tag, ServerLevel world) {
        if (!tag.contains("VTM_CoffinX")) return Optional.empty();
        BlockPos pos = new BlockPos(tag.getInt("VTM_CoffinX"), tag.getInt("VTM_CoffinY"), tag.getInt("VTM_CoffinZ"));
        
        if (!world.isLoaded(pos)) return Optional.empty();
        
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CoffinBlock && !isOccupied(state, pos, world, tag)) {
            BlockPos head = getHead(pos, state);
            if (head != null && world.getBlockState(head).getBlock() instanceof CoffinBlock) {
                return Optional.of(head.above());
            }
        }
        return Optional.empty();
    }

    private static BlockPos getCenter(CompoundTag tag, ServerLevel world) {
        int x = tag.getInt("VTM_CenterKeyX") * 16 + 8;
        int z = tag.getInt("VTM_CenterKeyZ") * 16 + 8;
        return world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));
    }

    private static boolean isOccupied(BlockState state, BlockPos pos, ServerLevel world, CompoundTag tag) {
        BlockPos h = getHead(pos, state);
        if (h == null || !world.isLoaded(h)) return true;
        BlockState hs = world.getBlockState(h);
        if (!(hs.getBlock() instanceof CoffinBlock)) return true;
        return hs.getValue(BedBlock.OCCUPIED) && !isMySleepingPos(h, tag);
    }

    private static boolean isMySleepingPos(BlockPos headPos, CompoundTag tag) {
        return true; 
    }

    private static BlockPos getHead(BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof CoffinBlock)) return null;
        if (state.getValue(CoffinBlock.PART) == CoffinPart.HEAD) return pos;
        Direction d = state.getValue(HORIZONTAL_FACING);
        return state.getValue(CoffinBlock.VERTICAL) ? pos.above() : pos.relative(d);
    }
}