package net.daanlokdrog.vampirismthemasquerade.subtitle;

import net.minecraft.world.entity.Entity;

//Incomplete

public final class SubtitleAPI {
    private static final String SUBTITLE_NBT_KEY = "vtm_subtitle";

    public static void setSubtitle(Entity entity, String translationKey) {
        entity.getPersistentData().putString(SUBTITLE_NBT_KEY, translationKey);
    }

    public static String getSubtitleKey(Entity entity) {
        return entity.getPersistentData().getString(SUBTITLE_NBT_KEY);
    }

    public static void clearSubtitle(Entity entity) {
        entity.getPersistentData().remove(SUBTITLE_NBT_KEY);
    }
}

