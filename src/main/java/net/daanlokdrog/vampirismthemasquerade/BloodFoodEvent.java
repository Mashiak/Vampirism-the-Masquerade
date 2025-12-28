package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class BloodFoodEvent {

    private static final String IB = "InjectedBlood";
    private static final String IT = "InjectedType";

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player == null) return;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        boolean isFood = off.getItem().getFoodProperties(off, player) != null;
        boolean isDrink = off.getItem() instanceof PotionItem;
        if (main.getItem() instanceof de.teamlapen.vampirism.items.BloodBottleItem && (isFood || isDrink)) {
            int blood = BloodHelper.getBlood(main);
            if (blood <= 0) return;
            BloodHelper.drain(main, blood,
            net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE,
            true,
            container -> player.setItemInHand(event.getHand(), container));
            ItemStack single = off.copy();
            single.setCount(1);
            CompoundTag tag = new CompoundTag();
            tag.putInt(IB, blood);
            tag.putString(IT, isFood ? "food" : "drink");
            single.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            MutableComponent name = single.getHoverName().copy().withStyle(ChatFormatting.DARK_RED);
            single.set(DataComponents.CUSTOM_NAME, name);
            off.shrink(1);
            if (!player.getInventory().add(single)) {
                player.drop(single, false);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFinishUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null) return;
        CompoundTag tag = cd.copyTag();
        if (!tag.contains(IB)) return;
        int blood = tag.getInt(IB);
        if (blood > 0) {
            VampirePlayer vampire = VampirePlayer.get(player);
          if (vampire != null) {
             int units = blood / VReference.FOOD_TO_FLUID_BLOOD;
             if (units > 0) {
               vampire.drinkBlood(units, 0.5F, false, new DrinkBloodContext(stack.copy()));
                }
            }
        }
        tag.remove(IB);
        tag.remove(IT);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_NAME, null);
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null) return;
        CompoundTag tag = cd.copyTag();
        if (tag.contains(IB)) {
            int blood = tag.getInt(IB);
            if (blood > 0) {
                MutableComponent label = Component.translatable("tooltip.vtm.injected_blood")
                        .withStyle(ChatFormatting.DARK_GREEN);
                Component value = Component.literal(String.valueOf(blood)).withStyle(ChatFormatting.DARK_RED);
                Component unit = Component.translatable("tooltip.vtm.ml").withStyle(ChatFormatting.DARK_RED);
                event.getToolTip().add(label.append(Component.literal(": ")).append(value).append(Component.literal(" ")).append(unit));
            }
        }
    }
}

