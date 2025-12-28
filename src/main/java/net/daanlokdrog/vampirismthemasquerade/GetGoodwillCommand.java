package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public class GetGoodwillCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("getgoodwill")
                    .executes(GetGoodwillCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        ChunkPos currentChunk = new ChunkPos(pos);

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
        ChunkPos centerKey = villageData.getCenterKeyForChunk(currentChunk);

        if (centerKey == null) {
            player.sendSystemMessage(Component.literal(
                "can't find village or domain mapping for current location."
            ));
            return 0;
        }

        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);

        if (!domainData.isDomainClaimed(centerKey)) {
             player.sendSystemMessage(Component.literal(
                "Village is found, but it is not currently claimed as a Domain. Domain data is unavailable."
            ));
            return 0;
        }

        double baseHabitability = domainData.getHabitability(centerKey);
        double effectiveHabitability = domainData.getEffectiveHabitability(level, centerKey);
        
        String baseValue = String.format("%.2f", baseHabitability);
        String effectiveValue = String.format("%.2f", effectiveHabitability);

        player.sendSystemMessage(Component.literal(
            "Domain [" + centerKey.x + "," + centerKey.z + "] Habitability Info:"
        ));
        player.sendSystemMessage(Component.literal(
            "  Base Habitability (Permanent): " + baseValue
        ));
        player.sendSystemMessage(Component.literal(
            "  Effective Habitability (Actual): " + effectiveValue + " (Base - Panic)"
        ));

        return 1;
    }
}