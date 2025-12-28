package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import org.jetbrains.annotations.Nullable;

public final class ThrallAPI {
    private ThrallAPI() {}

    public static boolean isThrall(Villager villager) {
        CompoundTag tag = villager.getPersistentData();
        return tag.getBoolean("VTM_IsThrall");
    }

    @Nullable
    public static String getOwnerUUID(Villager villager) {
        CompoundTag tag = villager.getPersistentData();
        if (!tag.getBoolean("VTM_IsThrall")) {
            return null;
        }
        String uuid = tag.getString("VTM_ThrallOwner");
        return uuid.isEmpty() ? null : uuid;
    }
}
