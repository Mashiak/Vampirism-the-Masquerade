package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.daanlokdrog.vampirismthemasquerade.DomainEdictStorage; 
import net.daanlokdrog.vampirismthemasquerade.MasqueradeDomainData;
import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData; 
import net.daanlokdrog.vampirismthemasquerade.VillagerWitnessEvent;
import net.daanlokdrog.vampirismthemasquerade.PanicEventEffects; 
import net.daanlokdrog.vampirismthemasquerade.DomainFeedingRestriction;
import net.daanlokdrog.vampirismthemasquerade.BloodOverlayMarker;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerMixin {

   @Inject(method = "biteEntity(I)V", at = @At("HEAD"), cancellable = true)
    private void vtm$checkEdictOnBiteEntity(int entityId, CallbackInfo ci) {
        VampirePlayer self = (VampirePlayer)(Object)this;
        Player player = self.getRepresentingPlayer();

        if (!player.level().isClientSide) {
            ServerLevel level = (ServerLevel) player.level();
            LivingEntity bittenEntity = (LivingEntity) level.getEntity(entityId);

            DomainFeedingRestriction.checkAndPunishFeeding(player, bittenEntity);
        }
    }

    @Inject(method = "biteEntity(I)V", at = @At("TAIL"))
    private void vtm$onBiteEntityEvent(int entityId, CallbackInfo ci) {
        VampirePlayer self = (VampirePlayer)(Object)this;
        Player player = self.getRepresentingPlayer();

        if (!player.level().isClientSide) {
            
            ServerLevel level = (ServerLevel) player.level();
            LivingEntity bittenEntity = (LivingEntity) level.getEntity(entityId);

            PanicEventEffects.applyBitePanicEffect(player, bittenEntity);
            VillagerWitnessEvent.handleWitness(player);
        }
    }

    @Inject(method = "updateFeeding()V", at = @At("TAIL"))
    private void onUpdateFeeding(CallbackInfo ci) {
        VampirePlayer self = (VampirePlayer)(Object)this;
        Player player = self.getRepresentingPlayer();

        if (!player.level().isClientSide) {
            VillagerWitnessEvent.handleWitness(player);
        }
    }

    @Inject(
        method = "biteBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
        at = @At("TAIL")
    )
    private void onBiteBlock(BlockPos pos, BlockState blockState, BlockEntity tileEntity, CallbackInfo ci) {
        VampirePlayer self = (VampirePlayer)(Object)this;
        Player player = self.getRepresentingPlayer();

        if (!player.level().isClientSide) {
            VillagerWitnessEvent.handleWitness(player);
            BloodOverlayMarker.markBloodied((ServerPlayer) player, 60);
        }
    }
}