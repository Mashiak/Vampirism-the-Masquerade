package net.daanlokdrog.vampirismthemasquerade.mixin;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.configuration.MasqueradeConfigConfiguration;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.daanlokdrog.vampirismthemasquerade.ModSoundEvents;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;

/**
 * When the vampire player dies, activate Beast Mode first before going DBNO.
 */
@Mixin(VampirePlayer.class)
public abstract class DBNOMixin {

    @Unique
    private Player actualPlayer; 

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"), remap = false)
    private void vtm_storePlayerInstance(Player player, CallbackInfo ci) {
        this.actualPlayer = player;
    }

    @Inject(
        method = "onDeadlyHit", 
        at = @At("HEAD"),
        cancellable = true,
        remap = false 
    )
    private void vtm_tryActivateBeastFormToSaveFromDBNO(
        @NotNull DamageSource source, 
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.actualPlayer == null || this.actualPlayer.level().isClientSide() || !(this.actualPlayer instanceof ServerPlayer)) {
            return;
        }

        if (!MasqueradeConfigConfiguration.BEAST_MODE.get()) {
            return;
        }

        VampirismTheMasqueradeModVariables.PlayerVariables vars =
            this.actualPlayer.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);

        if (vars == null) {
            return;
        }

        if (!vars.beast && vars.beast_cooldown <= 0) {
            vars.beast = true;
            this.actualPlayer.setHealth(this.actualPlayer.getMaxHealth());
            this.actualPlayer.sendSystemMessage(Component.translatable("vtm.dbno_save.beast_activated"));
            vars.markSyncDirty();

            ServerPlayer serverPlayer = (ServerPlayer) this.actualPlayer;
            ServerLevel level = serverPlayer.serverLevel();
            IPlayableFaction.TitleGender gender = FactionPlayerHandler.get(serverPlayer).titleGender();
            SoundEvent roar = (gender == IPlayableFaction.TitleGender.FEMALE)
                ? ModSoundEvents.VAMPIRE_F_ROAR.get()
                : ModSoundEvents.VAMPIRE_M_ROAR.get();
            level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), roar, SoundSource.PLAYERS, 1.0F, 1.0F);

AdvancementHolder advHolder = serverPlayer.server.getAdvancements()
    .get(ResourceLocation.parse("vampirism_the_masquerade:beastadvancement"));

if (advHolder != null) {
    AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advHolder);
    if (!progress.isDone()) {
        for (String criterion : progress.getRemainingCriteria()) {
            serverPlayer.getAdvancements().award(advHolder, criterion);
        }
    }
}

            cir.setReturnValue(true); 
            cir.cancel(); 
        }
    }
}
