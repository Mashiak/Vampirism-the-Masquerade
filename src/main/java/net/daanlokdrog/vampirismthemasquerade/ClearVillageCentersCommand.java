package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class ClearVillageCentersCommand {
	
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2)) 
                .then(Commands.literal("clearVillageCenters")
                    .then(Commands.argument("radius", IntegerArgumentType.integer(1, 256))
                        .executes(ClearVillageCentersCommand::execute)
                    )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        int radius = IntegerArgumentType.getInteger(context, "radius");

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        BlockPos playerPos = BlockPos.containing(source.getPosition());
        ChunkPos playerChunk = new ChunkPos(playerPos);
        Set<ChunkPos> uniqueCentersToClear = new HashSet<>();

        int chunkRadius = Math.max(1, radius / 16);
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                ChunkPos cp = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                ChunkPos centerKey = data.getCenterKeyForChunk(cp);
                if (centerKey != null) {
                    uniqueCentersToClear.add(centerKey);
                }
            }
        }
        
        int clearedCenterCount = 0;

        for (ChunkPos centerKey : uniqueCentersToClear) {
            BlockPos centerPos = data.getVillageCenter(centerKey);
            
            if (centerPos != null) {
                data.removeVillageCenter(centerKey); 
                clearedCenterCount++;
            } else {
                 data.removeVillageCenter(centerKey);
                 source.sendFailure(Component.literal("§c[Masquerade] 清理了一个损坏的中心键: [" + centerKey.x + ", " + centerKey.z + "]"));
            }
        }
        
        final int r = radius;
        final int c = clearedCenterCount;
        
        source.sendSuccess(() -> Component.literal(
            "§a[Masquerade] §6已在半径 " + r + "m 内成功清除了 " + c + " 个唯一的村庄中心。"
        ), true);

        return 1;
    }
}
