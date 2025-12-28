package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class StoredAdvisorData {
    public final CompoundTag advisorNBT;
    public final long deathTick;

    public StoredAdvisorData(CompoundTag advisorNBT, long deathTick) {
        this.advisorNBT = advisorNBT;
        this.deathTick = deathTick;
    }
    
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("AdvisorNBT", advisorNBT.copy());
        tag.putLong("DeathTick", deathTick);
        return tag;
    }

    public static StoredAdvisorData fromNBT(CompoundTag tag) {
        CompoundTag advisorNBT = tag.getCompound("AdvisorNBT");
        long deathTick = tag.getLong("DeathTick");
        return new StoredAdvisorData(advisorNBT, deathTick);
    }
}