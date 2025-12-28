package net.daanlokdrog.vampirismthemasquerade.configuration;

public class MasqueradeConfigHelper {
    public static int getEyeTypeCount() {
        return MasqueradeConfigConfiguration.EYE_TYPE.get().intValue();
    }

    public static int getFangTypeCount() {
        return MasqueradeConfigConfiguration.FANG_TYPE.get().intValue();
    }
}
