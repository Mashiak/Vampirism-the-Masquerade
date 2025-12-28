package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.daanlokdrog.vampirismthemasquerade.data.HumorData;
import net.daanlokdrog.vampirismthemasquerade.data.HumorType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(targets = "de.teamlapen.vampirism.client.renderer.RenderHandler")
public abstract class RenderHandlerBloodVisionMixin {

    @ModifyArgs(
        method = "onRenderLivingPost",
        at = @At(
            value = "INVOKE",
            target = "Lde/teamlapen/vampirism/client/renderer/RenderHandler$OutlineBuffer;setColor(IIII)V"
        )
    )
    private void vtm_modifySetColorArgs(Args args, RenderLivingEvent.Post<?, ?> event) {
        int r = args.get(0);
        int g = args.get(1);
        int b = args.get(2);
        int a = args.get(3);

        Entity entity = event.getEntity();
        Minecraft mc = Minecraft.getInstance();

        boolean garlicVision = VampirismPlayerAttributes.get(mc.player).getVampSpecial().blood_vision_garlic;
        Optional<ExtendedCreature> opt = entity instanceof PathfinderMob && entity.isAlive()
                ? ExtendedCreature.getSafe(entity) : Optional.empty();

        float relBlood = opt.filter(creature -> !creature.hasPoisonousBlood())
                .filter(creature -> creature.getBlood() > 0)
                .map(creature -> creature.getBlood() / (float) creature.getMaxBlood())
                .orElse(0F);

        boolean hasPoisonOrHunter = opt.map(IExtendedCreatureVampirism::hasPoisonousBlood).orElse(false)
                || (entity instanceof IHunterMob);

        int rr = r, gg = g, bb = b;

        if (relBlood == 0F && garlicVision && hasPoisonOrHunter) {
            rr = 0x07;
            gg = 0xE0;
            bb = 0x07;
        }
        else if (entity instanceof Villager villager) {
            int humorId = HumorData.getHumor(villager);
            if (humorId >= 0) {
                HumorType humor = HumorType.byId(humorId);
                int[] target = switch (humor) {
                    case PHLEGMATIC -> new int[]{0, 255, 255};
                    case CHOLERIC   -> new int[]{255, 96, 0};
                    case MELANCHOLIC-> new int[]{96, 0, 255};
                    case SANGUINE   -> new int[]{255, 120, 180};
                };
                int[] gray = new int[]{160, 160, 160};

                rr = Math.round((1 - relBlood) * gray[0] + relBlood * target[0]);
                gg = Math.round((1 - relBlood) * gray[1] + relBlood * target[1]);
                bb = Math.round((1 - relBlood) * gray[2] + relBlood * target[2]);
            }
        }

        args.set(0, rr);
        args.set(1, gg);
        args.set(2, bb);
        args.set(3, a);
    }
}
