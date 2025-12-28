package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class GetPanicCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("getpanic")
                    .executes(GetPanicCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        ChunkPos chunk = new ChunkPos(pos);

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        ChunkPos centerKey = data.getCenterKeyForChunk(chunk);
        Double panic = centerKey != null ? data.getPanicForChunk(centerKey) : null;

        if (panic != null) {
            player.sendSystemMessage(Component.literal(
                "Village [" + chunk.x + "," + chunk.z + "] Panic: " + panic
            ));
        } else {
            player.sendSystemMessage(Component.literal(
                "can't find village"
            ));
        }

        return 1;
    }
}
