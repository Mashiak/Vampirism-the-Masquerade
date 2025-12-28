package net.daanlokdrog.vampirismthemasquerade.client;

import net.daanlokdrog.vampirismthemasquerade.VampirismTheMasqueradeMod;
import net.daanlokdrog.vampirismthemasquerade.init.ModEntities;
import net.daanlokdrog.vampirismthemasquerade.VampireIllagerOverlayLayer;
import net.daanlokdrog.vampirismthemasquerade.VampireEvokerRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber
public class ModEntityRenderers {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.VAMPIRE_PILLAGER.get(), ctx -> {
            PillagerRenderer renderer = new PillagerRenderer(ctx);
            renderer.addLayer(new VampireIllagerOverlayLayer<>(renderer));
            return renderer;
        });

        event.registerEntityRenderer(ModEntities.VAMPIRE_VINDICATOR.get(), ctx -> {
            VindicatorRenderer renderer = new VindicatorRenderer(ctx);
            renderer.addLayer(new VampireIllagerOverlayLayer<>(renderer));
            return renderer;
        });

        event.registerEntityRenderer(ModEntities.VAMPIRE_EVOKER.get(), ctx -> {
            EvokerRenderer renderer = new EvokerRenderer(ctx);
            renderer.addLayer(new VampireEvokerRenderer<>(renderer));
            return renderer;
        });

        event.registerEntityRenderer(ModEntities.VAMPIRE_ILLUSIONER.get(), ctx -> {
            IllusionerRenderer renderer = new IllusionerRenderer(ctx);
            renderer.addLayer(new VampireIllagerOverlayLayer<>(renderer));
            return renderer;
        });
    }
}
