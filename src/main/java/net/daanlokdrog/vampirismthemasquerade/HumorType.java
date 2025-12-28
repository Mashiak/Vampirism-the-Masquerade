package net.daanlokdrog.vampirismthemasquerade.data;

import net.minecraft.network.chat.Component;

public enum HumorType {
    PHLEGMATIC("humor.vtm.phlegmatic"),
    CHOLERIC("humor.vtm.choleric"),
    SANGUINE("humor.vtm.sanguine"),
    MELANCHOLIC("humor.vtm.melancholic");

    private final String translationKey;

    HumorType(String key) {
        this.translationKey = key;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public static HumorType byId(int id) {
        HumorType[] values = values();
        return values[id % values.length];
    }
}
