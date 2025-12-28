package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables.PlayerVariables;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class DonationReward implements TaskReward, ITaskRewardInstance {

    public static final double DONATION = 5.0;

    public static final MapCodec<DonationReward> CODEC =
        MapCodec.unit(new DonationReward());

    private final Component description =
        Component.translatable("task_reward.masquerade.donation_reward", Component.literal(String.valueOf(DONATION * 2)));

//Not yet used. In the future, certain tasks will earn double donations.
    @Override
    public void applyReward(@NotNull IFactionPlayer<?> factionPlayer) {
        final double finalAmount = DONATION * 2;
        grantDonationInternal(factionPlayer, finalAmount);
        factionPlayer.asEntity().displayClientMessage(
            Component.translatable("text.masquerade.donation_success", Component.literal(String.valueOf(finalAmount))), true
        );
    }

    @Override
    public @NotNull ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public MapCodec<DonationReward> codec() {
        return MasqueradeTaskRewards.DONATION_REWARD.get();
    }

    @Override
    public Component description() {
        return this.description;
    }

    public static void applyInjectedReward(@NotNull IFactionPlayer<?> factionPlayer) {
        final double injectedAmount = DONATION;
        grantDonationInternal(factionPlayer, injectedAmount);
        factionPlayer.asEntity().displayClientMessage(
            Component.translatable("text.masquerade.donation_success", Component.literal(String.valueOf(injectedAmount))), true
        );
    }

    private static void grantDonationInternal(@NotNull IFactionPlayer<?> factionPlayer, double amount) {
        PlayerVariables vars = factionPlayer.asEntity()
            .getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
        if (vars != null) {
            vars.donation += amount;
            vars.markSyncDirty();
        }
    }
}