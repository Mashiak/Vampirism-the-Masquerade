package net.daanlokdrog.vampirismthemasquerade;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component; 
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class BeastCommand {
	
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .then(Commands.literal("setbeastcooldown")
                    .requires(s -> s.hasPermission(2))
                    .then(Commands.argument("minutes", IntegerArgumentType.integer(0))
                        .executes(BeastCommand::executeSetBeastCooldown)
                    )
                )
        );
    }

    private static int executeSetBeastCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getEntity();
        if (!(entity instanceof Player)) {
            source.sendFailure(Component.literal("此命令只能由玩家执行。")); 
            return 0;
        }

        ServerPlayer player = (ServerPlayer) entity;
        int minutes = IntegerArgumentType.getInteger(context, "minutes");
        int cooldownTicks = minutes * 1200;
        
        VampirismTheMasqueradeModVariables.PlayerVariables vars = 
            player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        vars.beast_cooldown = cooldownTicks;
        vars.markSyncDirty();
        
        String message = String.format("已将您的心兽冷却时间设置为 %d 分钟.", 
            minutes, cooldownTicks);
        source.sendSuccess(() -> Component.literal(message), true);
        if (cooldownTicks == 0) {
            source.sendSuccess(() -> Component.literal("心兽冷却已解除。"), true);
            if (vars.beast) {
                vars.beast = false;
                vars.markSyncDirty();
            }
        }
        
        return 1;
    }
}