package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

@EventBusSubscriber
public class AdvancedVampireAdvisorRecruiter {
    private static final Random rng = new Random();

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide || event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof AdvancedVampireEntity v)) return;

        CompoundTag nbt = v.getPersistentData();
        ServerPlayer p = (ServerPlayer) event.getEntity();

        if (nbt.getBoolean("IsAdvisor")) {
            InteractionResult res = AdvancedVampireAdvisorInteract.openNegotiationMenu(p, v);
            if (res != InteractionResult.PASS) {
                event.setCancellationResult(res);
                event.setCanceled(true);
            }
            return;
        }

        ItemStack held = p.getItemInHand(event.getHand());
        if (!(held.getItem() instanceof PureBloodItem)) return;

        ServerLevel world = p.serverLevel();
        var data = MasqueradeDomainData.get(world);
        ChunkPos pos = MasqueradeVillageData.get(world).getCenterKeyForChunk(v.chunkPosition());

        if (pos == null || !data.isDomainClaimed(pos) || !p.getUUID().toString().equals(data.getLordUUID(pos))) {
            shout(p, v, Component.translatable("vtm.advisor.error.not_in_domain"));
            return;
        }

        if (data.getEffectiveHabitability(world, pos) < 80) {
            String msgKey = data.getHabitability(pos) >= 80 ? "vtm.advisor.error.high_panic" : "vtm.advisor.error.low_habitability";
            shout(p, v, Component.translatable(msgKey, 80));
            return;
        }

        if (data.getCurrentAdvisorCount(pos) >= data.getMaxAdvisorCount(pos)) {
            shout(p, v, Component.translatable("vtm.advisor.error.max_advisors", data.getMaxAdvisorCount(pos)));
            return;
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        if (!p.getAbilities().instabuild) held.shrink(1);

        if (rng.nextFloat() < 0.8f) {
            int count = nbt.getInt("VTM_AdvisorProgress") + 1;
            nbt.putInt("VTM_AdvisorProgress", count);
            
            if (count >= 3) {
                nbt.putBoolean("IsAdvisor", true);
                nbt.remove("VTM_AdvisorProgress");
                nbt.putInt("VTM_CenterKeyX", pos.x);
                nbt.putInt("VTM_CenterKeyZ", pos.z);
                nbt.putString("VTM_LordUUID", p.getUUID().toString());

                shout(p, v, Component.translatable("vtm.advisor.success"));
                world.playSound(null, v.blockPosition(), ModSoundEvents.VAMPIRE_SERVICE.get(), SoundSource.PLAYERS, 1f, 1f + rng.nextFloat() * 0.2f);
                
                String name = data.getDomainName(pos);
                if (name != null && !name.isEmpty()) nbt.putString("VTM_AdvisorDomainName", name);

                v.setCustomName(v.getName().copy().withStyle(ChatFormatting.DARK_PURPLE));
                v.setCustomNameVisible(true);
                v.setPersistenceRequired();

                data.setCurrentAdvisorCount(pos, data.getCurrentAdvisorCount(pos) + 1);
                data.setDirty();
            }
        }
    }

    private static void shout(ServerPlayer p, AdvancedVampireEntity v, Component msg) {
        p.sendSystemMessage(v.getDisplayName().copy().withStyle(ChatFormatting.DARK_PURPLE)
                .append(Component.literal(": ").withStyle(ChatFormatting.WHITE))
                .append(msg.copy().withStyle(ChatFormatting.DARK_PURPLE)));
    }
}