package net.daanlokdrog.vampirismthemasquerade.client;

import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;

import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entity.layers.WingsLayer;

/**
 * The Vampire Wings appear on players in Beast Mode.
 * This is a temporary solution.
 * If the original mod decides to reapply the vampire wings, this class will be abandoned.
 */

@EventBusSubscriber
public class BeastWingRender {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModEntitiesRender.WING,
            de.teamlapen.vampirism.client.model.WingModel::createLayer);
    }

    @SubscribeEvent
    public static void addRenderLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer defaultRenderer = event.getSkin(PlayerSkin.Model.WIDE);
        if (defaultRenderer != null) {
            addWingsToRenderer(defaultRenderer, event.getEntityModels());
        }

        PlayerRenderer slimRenderer = event.getSkin(PlayerSkin.Model.SLIM);
        if (slimRenderer != null) {
            addWingsToRenderer(slimRenderer, event.getEntityModels());
        }
    }

    private static void addWingsToRenderer(PlayerRenderer playerRenderer, EntityModelSet modelSet) {
        WingsLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> wingsLayer = new WingsLayer<>(
            playerRenderer, 
            modelSet, 
            (AbstractClientPlayer entity) -> {
                VampirismTheMasqueradeModVariables.PlayerVariables vars = 
                    entity.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES);
                return vars.beast && vars.beast_cooldown == 0;
            },
            (AbstractClientPlayer entity, PlayerModel<AbstractClientPlayer> model) -> {
                return model.body;
            }
        );
        playerRenderer.addLayer(wingsLayer);
    }
}