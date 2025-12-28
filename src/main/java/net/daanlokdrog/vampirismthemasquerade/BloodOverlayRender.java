package net.daanlokdrog.vampirismthemasquerade.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(value = Dist.CLIENT)
public class BloodOverlayRender {

    private static final ResourceLocation[] BLOOD_OVERLAYS = new ResourceLocation[] {
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face1.png"),
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face2.png"),
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face3.png"),
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face4.png"),
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face5.png"),
        ResourceLocation.parse("vampirism_the_masquerade:textures/overlay/blood_face6.png")
    };

    @SubscribeEvent
    public static void addRenderLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer defaultRenderer = event.getSkin(PlayerSkin.Model.WIDE);
        if (defaultRenderer != null) {
            defaultRenderer.addLayer(new BloodOverlayLayer(defaultRenderer, BLOOD_OVERLAYS));
        }

        PlayerRenderer slimRenderer = event.getSkin(PlayerSkin.Model.SLIM);
        if (slimRenderer != null) {
            slimRenderer.addLayer(new BloodOverlayLayer(slimRenderer, BLOOD_OVERLAYS));
        }
    }
}
