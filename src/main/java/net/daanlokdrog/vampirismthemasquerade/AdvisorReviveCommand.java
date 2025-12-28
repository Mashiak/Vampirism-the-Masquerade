package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.List;
import java.util.function.Supplier;

public class AdvisorReviveCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("masquerade")
                .requires((source) -> source.hasPermission(2)) 
                .then(Commands.literal("reviveadvisor")
                    .executes(context -> reviveAdvisor(context.getSource(), null)) 
                    .then(Commands.argument("DomainX", IntegerArgumentType.integer())
                        .then(Commands.argument("DomainZ", IntegerArgumentType.integer())
                            .executes(context -> reviveAdvisor(context.getSource(), 
                                new ChunkPos(
                                    IntegerArgumentType.getInteger(context, "DomainX"),
                                    IntegerArgumentType.getInteger(context, "DomainZ")
                                )
                            ))
                        )
                    )
                )
        );
    }

    private static int reviveAdvisor(CommandSourceStack source, ChunkPos targetCenterKey) throws CommandSyntaxException {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("此指令只能由玩家执行。"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        ChunkPos centerKey;

        MasqueradeVillageData villageData = MasqueradeVillageData.get(level);

        if (targetCenterKey == null) {
            centerKey = villageData.getCenterKeyForChunk(new ChunkPos(player.blockPosition()));
            
            if (centerKey == null) {
                source.sendFailure(Component.literal("您当前位置不属于任何已识别的领域。请提供领域中心坐标。").withStyle(ChatFormatting.RED));
                return 0;
            }
        } else {
            centerKey = targetCenterKey;
        }
        
        MasqueradeDomainData domainData = MasqueradeDomainData.get(level);
        DeadAdvisorStorage storage = DeadAdvisorStorage.get(level);

        if (!domainData.isDomainClaimed(centerKey)) {
            source.sendFailure(Component.literal("目标 ChunkPos [" + centerKey.x + ", " + centerKey.z + "] 处没有认领的领域。").withStyle(ChatFormatting.RED));
            return 0;
        }

        List<StoredAdvisorData> deadAdvisors = storage.getDAFD(centerKey); 
        
        if (deadAdvisors.isEmpty()) {
            source.sendSuccess(() -> Component.literal("领域 [" + centerKey.x + ", " + centerKey.z + "] 中没有等待复活的顾问。"), false);
            return 1;
        }

        StoredAdvisorData dataToRevive = deadAdvisors.get(0);
        
        boolean success = AdvisorRespawnManager.forceReviveSpecificAdvisor(centerKey, player, level, domainData, storage, dataToRevive);
        
        if (success) {
            String advisorName = dataToRevive.advisorNBT.contains("CustomName") ? dataToRevive.advisorNBT.getString("CustomName") : "未知顾问";
            source.sendSuccess(() -> Component.literal("成功强行复活了顾问: " + advisorName).withStyle(ChatFormatting.GREEN), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("复活顾问失败。请检查日志。").withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}