package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component; 
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.daanlokdrog.vampirismthemasquerade.MasqueradeDomainData;
import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

import java.util.Optional;
import java.util.UUID;

public class UnbindDomainCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("masquerade_domain")
            .then(Commands.literal("unbind")
                .requires(s -> s.hasPermission(2)) 
                .executes(UnbindDomainCommand::execute)
            )
        );
    }

    private static void updateLordTerritories(ServerPlayer lord) {
        VampirismTheMasqueradeModVariables.PlayerVariables lordVars = lord.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        
        if (lordVars.territories > 0) {
            lordVars.territories -= 1;
        }

        if (lordVars.territories <= 0) {
            lordVars.hasTerritory = false;
            lordVars.territories = 0;
        }

        lordVars.markSyncDirty();
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getEntity();

        if (!(entity instanceof Player)) {
            source.sendFailure(Component.translatable("vtm.command.unbind.only_player")); 
            return 0;
        }
        
        ServerPlayer executor = (ServerPlayer) entity;
        ServerLevel level = source.getLevel();
        BlockPos playerPos = executor.blockPosition();
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);
        MasqueradeVillageData villageData = MasqueradeVillageData.get(level); 
        ChunkPos currentChunk = new ChunkPos(playerPos);
        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);

        if (villageCenterKey == null) {
            source.sendFailure(Component.translatable("vtm.command.unbind.no_village"));
            return 0;
        }

        if (!domainData.isDomainClaimed(villageCenterKey)) {
            source.sendSuccess(() -> Component.translatable("vtm.command.unbind.not_claimed"), false);
            return 1;
        }

        String originalLordName = domainData.getLordName(villageCenterKey);
        String originalLordUUIDString = domainData.getLordUUID(villageCenterKey);
        Optional<UUID> originalLordUUID = domainData.getLordUUIDObject(villageCenterKey);
        String executorUUIDString = executor.getUUID().toString();
        boolean isAdmin = source.hasPermission(4);
        boolean isCurrentLord = executorUUIDString.equals(originalLordUUIDString); 

        if (!isAdmin && !isCurrentLord) {
            source.sendFailure(Component.translatable("vtm.command.unbind.not_lord", originalLordName));
            return 0;
        }
        
        if (isAdmin && !isCurrentLord) {
            source.sendSuccess(() -> Component.translatable("vtm.command.unbind.admin_override"), false);
        }

        domainData.unbindLord(villageCenterKey);
        domainData.setDirty();

        if (originalLordUUID.isPresent()) {
            ServerPlayer originalLord = level.getServer().getPlayerList().getPlayer(originalLordUUID.get());

            if (originalLord != null) {
                updateLordTerritories(originalLord);

                if (isAdmin && !isCurrentLord) {
                    source.sendSuccess(() -> Component.translatable("vtm.command.unbind.lord_vars_updated", originalLordName), false);
                }
            } else {
                if (isAdmin && !isCurrentLord) {
                    source.sendSuccess(() -> Component.translatable("vtm.command.unbind.lord_offline_warning", originalLordName), false);
                } 
            }
        }

        source.sendSuccess(() -> Component.translatable("vtm.command.unbind.success"), true);

        return 1;
    }
}