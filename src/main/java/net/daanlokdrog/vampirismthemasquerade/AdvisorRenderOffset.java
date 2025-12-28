package net.daanlokdrog.vampirismthemasquerade.client;

import net.neoforged.fml.common.EventBusSubscriber;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.minecraft.world.entity.LivingEntity;

@EventBusSubscriber
public class AdvisorRenderOffset {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, ?> event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof AdvancedVampireEntity advisor) {
            if (advisor.isSleeping()) {
                PoseStack poseStack = event.getPoseStack();
                poseStack.translate(0.0D, -0.4D, 0.0D);
            }
        }
    }
}