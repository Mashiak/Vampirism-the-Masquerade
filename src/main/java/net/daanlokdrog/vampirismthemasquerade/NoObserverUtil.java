package net.daanlokdrog.vampirismthemasquerade.util;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.fml.ModList;

public class NoObserverUtil {

    public static boolean isNoObserver(Entity e) {
        if (e instanceof ConvertedVillagerEntity) {
            return true;
        }

        if (e instanceof Villager && e.getPersistentData().getBoolean("VTM_IsThrall")) {
            return true;
        }

        if (ModList.get().isLoaded("vampirism_integrations")) {
            try {
                Class<?> mcaClass = Class.forName("de.teamlapen.vampirism_integrations.mca.ConvertedVillagerEntityMCA");
                if (mcaClass.isInstance(e)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return false;
    }
}
