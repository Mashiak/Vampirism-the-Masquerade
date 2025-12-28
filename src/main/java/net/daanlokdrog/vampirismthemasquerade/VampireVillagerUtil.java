package net.daanlokdrog.vampirismthemasquerade.util;

import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;

public class VampireVillagerUtil {

    public static boolean isVampireVillager(Entity e) {
        if (e instanceof ConvertedVillagerEntity) {
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
