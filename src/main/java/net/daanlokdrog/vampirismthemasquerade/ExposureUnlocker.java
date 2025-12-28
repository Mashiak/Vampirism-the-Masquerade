package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.minecraft.network.chat.Component;

public class ExposureUnlocker implements TaskUnlocker {

    public enum Mode {
        NOVICE, ADEPT, LORD
    }

    public static final MapCodec<ExposureUnlocker> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("exposureThreshold").forGetter(i -> i.exposureThreshold),
            Codec.STRING.fieldOf("mode").xmap(Mode::valueOf, Mode::name).forGetter(i -> i.mode),
            Codec.BOOL.fieldOf("matchExactly").forGetter(i -> i.matchExactly)
    ).apply(inst, ExposureUnlocker::new));

    private final int exposureThreshold;
    private final Mode mode;
    private final boolean matchExactly;

    public ExposureUnlocker(int exposureThreshold, Mode mode, boolean matchExactly) {
        this.exposureThreshold = exposureThreshold;
        this.mode = mode;
        this.matchExactly = matchExactly;
    }

    @Override
    public Component getDescription() {
        return Component.translatable("unlocker.masquerade.exposure",
                exposureThreshold, mode.name(), matchExactly);
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        var player = playerEntity.asEntity();
        var vars = player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars == null) return false;

        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        int vampireLevel = atts.vampireLevel;
        int lordLevel = atts.lordLevel;
        boolean unlockedCondition =
                (vars.exposure >= exposureThreshold) ||
                vars.huntActive ||
                vars.huntBegining;

        return switch (mode) {
            case LORD -> unlockedCondition &&
                         (matchExactly ? lordLevel == 1 : lordLevel > 0);

            case ADEPT -> unlockedCondition &&
                          lordLevel == 0 &&
                          (matchExactly ? vampireLevel == 10 : vampireLevel >= 10 && vampireLevel <= 14);

            case NOVICE -> unlockedCondition &&
                           lordLevel == 0 &&
                           (matchExactly ? vampireLevel == 1 : vampireLevel < 10);
        };
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return MasqueradeTask.EXPOSURE_UNLOCKER.get();
    }
}
