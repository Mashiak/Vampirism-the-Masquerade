package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public class DumpVillageChunksCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("dumpVillageChunks")
                    .executes(DumpVillageChunksCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        Set<ChunkPos> chunks = data.getVillageChunks();

        if (chunks.isEmpty()) {
            source.sendSuccess(() -> Component.literal("[Masquerade] 当前没有任何区块被标记为村庄"), false);
        } else {
            source.sendSuccess(() -> Component.literal("[Masquerade] 已标记的村庄区块数量=" + chunks.size()), false);
            for (ChunkPos cp : chunks) {
                source.sendSuccess(() -> Component.literal(" - 区块坐标: " + cp.x + "," + cp.z), false);
            }
        }

        return 1;
    }
}
