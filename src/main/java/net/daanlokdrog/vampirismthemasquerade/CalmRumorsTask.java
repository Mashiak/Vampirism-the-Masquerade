package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.api.VReference; 
import de.teamlapen.vampirism.api.entity.factions.IFaction; 
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IFactionMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.minion.management.DefaultMinionTask;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.RegUtil;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import net.daanlokdrog.vampirismthemasquerade.MasqueradeVillageData; 

public class CalmRumorsTask<Q extends IMinionData> extends DefaultMinionTask<CalmRumorsTask.Desc<Q>, Q> implements IFactionMinionTask<CalmRumorsTask.Desc<Q>, Q> {

    private final Function<Q, Integer> cooldownSupplier;

    public CalmRumorsTask(Function<Q, Integer> cooldownSupplier, Supplier<? extends ISkill<?>> requiredSkill) {
        super(requiredSkill);
        this.cooldownSupplier = cooldownSupplier;
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable(Util.makeDescriptionId("minion_task", RegUtil.id(this)));
    }

    @Override
    public void deactivateTask(Desc<Q> desc) {
    }

    @Override
    public @Nullable IFaction<?> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public @Nullable Desc<Q> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data) {
        this.triggerAdvancements(lord);
        
        if (minion != null) minion.recallMinion();

        ChunkPos pos = null;
        if (lord != null) {
            pos = new ChunkPos(lord.blockPosition());
            String baseKey = Util.makeDescriptionId("minion_task", RegUtil.id(this));
            lord.displayClientMessage(Component.translatable(baseKey + ".start"), true);
        }

        return new Desc<>(this, cooldownSupplier.apply(data), lord != null ? lord.getUUID() : null, pos);
    }

    @Override
    public boolean isAvailable(@NotNull IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return faction == VReference.VAMPIRE_FACTION && isRequiredSkillUnlocked(faction, player);
    }

    @Override
    public @NotNull Desc<Q> readFromNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        UUID lordId = nbt.hasUUID("lordid") ? nbt.getUUID("lordid") : null;
        ChunkPos chunkPos = nbt.contains("chunk_x") ? new ChunkPos(nbt.getInt("chunk_x"), nbt.getInt("chunk_z")) : null;

        return new Desc<>(this, nbt.getInt("cooldown"), lordId, chunkPos);
    }

    private double getDecayAmountPerCycle(int lordLevel) {
        return 0.1; 
    }

    @Override
    public void tickBackground(@NotNull Desc<Q> desc, @NotNull Q data) {

        if (--desc.coolDown <= 0) {
            int cycleDurationTicks = cooldownSupplier.apply(data); 
            desc.coolDown = cycleDurationTicks; 

            if (desc.lordEntityID != null && desc.activationChunk != null && ServerLifecycleHooks.getCurrentServer() != null) {
                
                    ServerLevel level = ServerLifecycleHooks.getCurrentServer().overworld(); 
                
                    if (level != null) {
                        ServerPlayer player = level.getServer().getPlayerList().getPlayer(desc.lordEntityID);
                        
                        int lordLevel = 0;
                        
                        if (player != null) {
                            lordLevel = VampirismPlayerAttributes.get(player).lordLevel;
                        } else {
                            lordLevel = 1; 
                        }
                        
                        if (lordLevel > 0) {
                            double decayAmountToApply = getDecayAmountPerCycle(lordLevel); 
                            double decayAmount = -decayAmountToApply; 
                            
                            ChunkPos centerKey = desc.activationChunk; 
                            
                            if (centerKey != null) { 
                                
                                MasqueradeVillageData villageData = MasqueradeVillageData.get(level);
                                villageData.addPanicForChunk(centerKey, decayAmount); 
                                villageData.setDirty(); 
                            }
                        }
                    }
            }
        }
    }

    public static class Desc<Z extends IMinionData> implements IMinionTask.IMinionTaskDesc<Z> {
        private final CalmRumorsTask<Z> task;
        @Nullable
        public final UUID lordEntityID;
        @Nullable
        public final ChunkPos activationChunk; 
        public int coolDown;

        public Desc(CalmRumorsTask<Z> task, int coolDown, @Nullable UUID lordEntityID, @Nullable ChunkPos activationChunk) {
            this.task = task;
            this.coolDown = coolDown;
            this.lordEntityID = lordEntityID;
            this.activationChunk = activationChunk;
        }

        @Override
        public IMinionTask<?, Z> getTask() {
            return task;
        }

        @Override
        public void writeToNBT(@NotNull CompoundTag nbt) {
            nbt.putInt("cooldown", coolDown);
            if (lordEntityID != null) {
                nbt.putUUID("lordid", lordEntityID);
            }
            if (activationChunk != null) {
                nbt.putInt("chunk_x", activationChunk.x);
                nbt.putInt("chunk_z", activationChunk.z);
            }
        }
    }
}