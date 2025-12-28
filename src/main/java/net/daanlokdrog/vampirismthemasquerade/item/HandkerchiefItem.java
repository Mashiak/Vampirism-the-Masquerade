package net.daanlokdrog.vampirismthemasquerade.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.world.entity.HumanoidArm;

@EventBusSubscriber(value = Dist.CLIENT)
public class HandkerchiefItem extends Item {
    public HandkerchiefItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(8)
                .setNoRepair());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 60;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        boolean cleared = false;

        if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
            VampirismTheMasqueradeModVariables.PlayerVariables vars =
                serverPlayer.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

            vars.bloodiedUntil = 0;
            vars.bloodOverlayIndex = -1;
            vars.markSyncDirty();

             cleared = true;
        }

        if (cleared) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            if (entity instanceof Player player) {
                player.getCooldowns().addCooldown(this, 20);
            }
        }

        return stack;
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof HandkerchiefItem && player.isUsingItem()) {
            event.setCanceled(true);

            PoseStack pose = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();

            pose.pushPose();
            pose.translate(0.0F, -0.5F, -0.5F);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                event.getPackedLight(),
                OverlayTexture.NO_OVERLAY,
                pose,
                buffer,
                player.level(),
                0
            );
            pose.popPose();
        }
    }
}

