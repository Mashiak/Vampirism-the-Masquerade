package net.daanlokdrog.vampirismthemasquerade.data;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;

public final class HumorData {
    private HumorData() {}

    public static int getHumor(Villager villager) {
        return ((HumorDataHolder) villager).vtm_getHumor();
    }

    public static void setHumor(Villager villager, int value) {
        ((HumorDataHolder) villager).vtm_setHumor(value);
    }

    public static Component getHumorName(int value) {
        return HumorType.byId(value).getDisplayName();
    }
}
