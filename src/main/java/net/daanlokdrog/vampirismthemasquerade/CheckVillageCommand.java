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

public class CheckVillageCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("checkVillage")
                    .executes(CheckVillageCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        int radius = 48;
        
        VillageMarker.scanAndMarkArea(level, pos, radius);

        int beds = VillageHelper.getBedCount(level, pos, radius);
        int villagers = VillageHelper.getVillagerCount(level, pos, radius);
        boolean hasVillage = VillageHelper.hasVillageNearby(level, pos, radius);
        ChunkPos chunkPos = new ChunkPos(pos);
        MasqueradeVillageData data = MasqueradeVillageData.get(level);
        boolean flagged = data.isVillageChunk(chunkPos);

        source.sendSuccess(() -> Component.literal(
            "[Masquerade] Beds=" + beds +
            "Villagers=" + villagers +
            "is Village=" + hasVillage +
            "Marked village=" + flagged
        ), false);

        return 1;
    }
}
