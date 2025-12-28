package net.daanlokdrog.vampirismthemasquerade.item;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModItems;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
//import net.daanlokdrog.vampirismthemasquerade.capability.ThrallAttachment;

@EventBusSubscriber
public class ThrallTagItem extends Item {

    public ThrallTagItem() {
        super(new Item.Properties());
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!Helper.isVampire(player)) return;

        ItemStack held = player.getItemInHand(event.getHand());
        if (!(held.getItem() instanceof ThrallTagItem)) return;

        if (!(event.getTarget() instanceof Villager villager) || villager instanceof ConvertedVillagerEntity) return;
        if (net.daanlokdrog.vampirismthemasquerade.data.ThrallData.isThrall(villager)) return;

        LivingEntity lastAttacker = villager.getLastHurtByMob();
        if (!(lastAttacker instanceof Player) || !lastAttacker.getUUID().equals(player.getUUID())) {
            player.sendSystemMessage(Component.translatable("vtm.thrall.error.not_last_attacker").withStyle(ChatFormatting.GRAY));
            return;
        }

        villager.getPersistentData().putBoolean("VTM_IsThrall", true);
        net.daanlokdrog.vampirismthemasquerade.data.ThrallData.setThrall(villager, true);

        villager.getPersistentData().putString("vtm_subtitle", "vtm.subtitle.thrall");
        villager.getPersistentData().putString("VTM_ThrallOwner", player.getUUID().toString());

        villager.goalSelector.addGoal(0,
            new TemptGoal(villager, 1.1, Ingredient.of(VampirismTheMasqueradeModItems.THRALL_BELL.get()), false));

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        player.sendSystemMessage(Component.translatable("vtm.thrall.success").withStyle(ChatFormatting.DARK_PURPLE));
        AdvancementHolder advHolder = player.server.getAdvancements()
            .get(ResourceLocation.parse("vampirism_the_masquerade:thrall_advancement"));
        if (advHolder != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advHolder);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advHolder, criterion);
                }
            }
        }

        event.setCancellationResult(InteractionResult.CONSUME);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (villager instanceof ConvertedVillagerEntity) return;
        if (net.daanlokdrog.vampirismthemasquerade.data.ThrallData.isThrall(villager)) {
            villager.goalSelector.addGoal(0,
                new TemptGoal(villager, 1.1, Ingredient.of(VampirismTheMasqueradeModItems.THRALL_BELL.get()), false));
        }
    }
}

