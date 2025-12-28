package net.daanlokdrog.vampirismthemasquerade.data;

import net.minecraft.world.entity.npc.Villager;

public final class ThrallData {
    private ThrallData() {}

    public static boolean isThrall(Villager villager) {
        return ((ThrallDataHolder) villager).vtm_isThrall();
    }

    public static void setThrall(Villager villager, boolean value) {
        ((ThrallDataHolder) villager).vtm_setThrall(value);
    }
}
