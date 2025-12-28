package net.daanlokdrog.vampirismthemasquerade.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.ChatFormatting;
import java.util.List;

import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;


import net.daanlokdrog.vampirismthemasquerade.MasqueradeDomainData;
import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;

import java.util.concurrent.ThreadLocalRandom;

public class TerritoryCertificateItem extends Item {

    private static final String DEFAULT_ITEM_TRANSLATION_KEY = "item.vampirism_the_masquerade.territory_certificate";


    public TerritoryCertificateItem() {
        super(new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override 
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag); 
        tooltip.add(Component.translatable("tooltip.vampirism_the_masquerade.territory_certificate_name_hint").withStyle(ChatFormatting.GRAY));
    }

    private static String generateRandomDomainNameSuffix() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();

        if (world.isClientSide() || player == null || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(serverPlayer);
        VampirismTheMasqueradeModVariables.PlayerVariables playerVars = serverPlayer.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        int vampireLevel = atts.vampireLevel;
        int lordLevel = atts.lordLevel;
        double currentTerritories = playerVars.territories;

        if (vampireLevel <= 0) {
            serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.error.not_vampire"));
            return InteractionResult.FAIL;
        }

        final int MIN_VAMP_LEVEL = 7;
        if (vampireLevel < MIN_VAMP_LEVEL) {
            serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.error.low_vampire_level", MIN_VAMP_LEVEL));
            return InteractionResult.FAIL;
        }

        int maxTerritories;
        if (lordLevel == 0) {
            maxTerritories = 2;
        } else {
            maxTerritories = 2 + lordLevel;
        }

        if (currentTerritories >= maxTerritories) {
            serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.error.max_territories", maxTerritories));
            return InteractionResult.FAIL;
        }

        ServerLevel serverLevel = (ServerLevel) world;
        BlockPos playerPos = serverPlayer.blockPosition();
        ChunkPos currentChunk = new ChunkPos(playerPos);
        MasqueradeVillageData villageData = MasqueradeVillageData.get(serverLevel);
        MasqueradeDomainData domainData = MasqueradeDomainData.get(serverLevel);
        ChunkPos villageCenterKey = villageData.getCenterKeyForChunk(currentChunk);

        if (villageCenterKey == null) {
            serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.error.no_village"));
            return InteractionResult.FAIL;
        }

        if (domainData.isDomainClaimed(villageCenterKey)) {
            String existingLord = domainData.getLordName(villageCenterKey);
            serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.error.already_claimed", existingLord));
            return InteractionResult.FAIL;
        }

        String lordName = serverPlayer.getName().getString();
        String lordUUID = serverPlayer.getUUID().toString();
        String domainName;
        IPlayableFaction.TitleGender gender = FactionPlayerHandler.get(serverPlayer).titleGender();
        boolean isLadyLord = gender == IPlayableFaction.TitleGender.FEMALE;
        Component hoverName = itemstack.getHoverName();
        Component defaultName = itemstack.getItem().getDescription();
        
        if (!hoverName.equals(defaultName)) {
            domainName = hoverName.getString();
        } else {
            String prefix = Component.translatable("domain.vampirism_the_masquerade.default_prefix").getString();
            domainName = prefix + generateRandomDomainNameSuffix();
        }
        
        domainData.setLordIdentity(villageCenterKey, lordName, lordUUID); 
        domainData.setVampireLevel(villageCenterKey, vampireLevel);
        domainData.setLordLevel(villageCenterKey, lordLevel);
        domainData.setIsLadyLord(villageCenterKey, isLadyLord);
        domainData.setDomainName(villageCenterKey, domainName);
        playerVars.territories += 1;
        playerVars.hasTerritory = true;
        playerVars.markSyncDirty();
        itemstack.shrink(1);
        serverPlayer.sendSystemMessage(Component.translatable("vtm.claim.success", domainName));

        domainData.setDirty();
        return InteractionResult.SUCCESS;
    }
}