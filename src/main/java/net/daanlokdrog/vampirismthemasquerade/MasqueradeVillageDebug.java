package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

public class MasqueradeVillageDebug {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("checkVillageChunk")
                    .executes(MasqueradeVillageDebug::checkVillageChunk))
        );
    }

    private static int checkVillageChunk(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(pos);
        VillageMarker.scanAndMarkArea(level, pos, 48);
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        boolean flagged = data.isVillageChunk(chunkPos);

        source.sendSuccess(() -> Component.literal(
            "[Masquerade] 当前区块 " + chunkPos.x + "," + chunkPos.z +
            " 村庄标记=" + flagged
        ), false);

        return 1;
    }
}
