package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GetVillageCenterCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.getEntity() instanceof ServerPlayer) 
                .then(Commands.literal("getVillageCenter")
                    .executes(GetVillageCenterCommand::execute)
                )
        );
    }

private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayerOrException();
    ServerLevel level = player.serverLevel();
    BlockPos playerPos = player.blockPosition();
    ChunkPos chunk = new ChunkPos(playerPos);

    MasqueradeVillageData data = MasqueradeVillageData.get(level);

    ChunkPos centerKey = data.getCenterKeyForChunk(chunk);
    BlockPos center = centerKey != null ? data.getVillageCenter(centerKey) : null;

    if (center != null) {
        source.sendSuccess(() -> Component.literal(
            "Chunk [" + chunk.x + "," + chunk.z + "] 's village center: "
            + center.getX() + ", " + center.getY() + ", " + center.getZ()
        ), false);
    } else {
        source.sendSuccess(() -> Component.literal(
            "There are no recorded village centers at this location"
        ), false);
    }

    return 1;
}

}