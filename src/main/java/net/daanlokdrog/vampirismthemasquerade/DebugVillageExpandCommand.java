package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import net.daanlokdrog.vampirismthemasquerade.VillageMarker;

public class DebugVillageExpandCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("debugVillageExpand")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 32))
                    .executes(DebugVillageExpandCommand::execute))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getLevel();

        int radius = IntegerArgumentType.getInteger(ctx, "radius");
        BlockPos pos = player.blockPosition();
        ChunkPos centerKey = new ChunkPos(pos);

        VillageMarker.debugExpandVillage(level, pos, centerKey, radius, player);

        source.sendSuccess(() -> Component.literal(
            "已执行 debugVillageExpand，半径 " + radius + " 区块"
        ), true);

        return Command.SINGLE_SUCCESS;
    }
}
