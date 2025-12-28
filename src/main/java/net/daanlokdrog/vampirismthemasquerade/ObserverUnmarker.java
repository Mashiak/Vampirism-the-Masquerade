package net.daanlokdrog.vampirismthemasquerade.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
//import net.daanlokdrog.vampirismthemasquerade.capability.ThrallAttachment;

public class ObserverUnmarker {

    private static final String VO = "VTM_IsObserverVillager";

    public static void removeObserverMark(Entity entity) {
        CompoundTag tag = entity.getPersistentData();

        if (tag.contains(VO)) {
            tag.remove(VO);
        }

        if (entity instanceof Villager villager) {
            net.daanlokdrog.vampirismthemasquerade.data.ThrallData.setThrall(villager, false);
            tag.remove("vtm_subtitle");
            tag.remove("VTM_ThrallOwner");
        }
    }
}
