package net.daanlokdrog.vampirismthemasquerade;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager; 
import net.minecraft.world.entity.npc.VillagerProfession; 

import de.teamlapen.vampirism.util.Helper;
import net.minecraft.network.chat.Component;

@EventBusSubscriber
public class TradePanicReduction {
    
    @SubscribeEvent
    public static void onTradeWithPlayer(TradeWithVillagerEvent event) {
        Player player = event.getAbstractVillager().getTradingPlayer();
        
        if (player == null || player.level().isClientSide() || !Helper.isVampire(player)) {
            return;
        }

        MerchantOffer offer = event.getMerchantOffer();
        AbstractVillager abstractVillager = event.getAbstractVillager();
        int emeraldCost = 0;
        
        ItemStack itemA = offer.getCostA();
        if (itemA.is(Items.EMERALD)) {
            emeraldCost += itemA.getCount();
        }
        ItemStack itemB = offer.getCostB();
        if (!itemB.isEmpty() && itemB.is(Items.EMERALD)) {
            emeraldCost += itemB.getCount();
        }

        double panicReduction = emeraldCost * 0.1;

        if (abstractVillager instanceof Villager villager) {
            if (villager.getVillagerData().getProfession() == VillagerProfession.CLERIC) {
                panicReduction += 0.5;
            }
        }
        if (panicReduction == 0.0) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        BlockPos pos = player.blockPosition();
        MasqueradeVillageData data = MasqueradeVillageData.get(serverLevel);
        ChunkPos cp = new ChunkPos(pos);
        ChunkPos centerKey = data.getCenterKeyForChunk(cp);

        if (centerKey != null) {
            data.addPanicForChunk(centerKey, -panicReduction);
        }
    }
}