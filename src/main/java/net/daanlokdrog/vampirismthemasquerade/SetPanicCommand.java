package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;


public class SetPanicCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setpanic")
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                        .executes(context -> execute(context))
                    )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            BlockPos pos = player.blockPosition();

            double value = DoubleArgumentType.getDouble(context, "value");

            MasqueradeVillageData data = MasqueradeVillageData.get(player.serverLevel());
            data.setPanicForChunk(new ChunkPos(pos), value);

            player.sendSystemMessage(Component.literal("Set panic: " + value));
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Set panic failed: " + e.getMessage()));
        }
        return 1;
    }
}
