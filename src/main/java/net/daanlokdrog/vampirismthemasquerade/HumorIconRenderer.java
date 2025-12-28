package net.daanlokdrog.vampirismthemasquerade.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.daanlokdrog.vampirismthemasquerade.data.HumorData;
import net.daanlokdrog.vampirismthemasquerade.data.HumorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = "vampirism_the_masquerade")
public class HumorIconRenderer {

    private static final ResourceLocation PHLEGMATIC_ICON =
            ResourceLocation.parse("vampirism_the_masquerade:textures/gui/phlegmatic.png");
    private static final ResourceLocation CHOLERIC_ICON =
            ResourceLocation.parse("vampirism_the_masquerade:textures/gui/choleric.png");
    private static final ResourceLocation MELANCHOLIC_ICON =
            ResourceLocation.parse("vampirism_the_masquerade:textures/gui/melancholic.png");
    private static final ResourceLocation SANGUINE_ICON =
            ResourceLocation.parse("vampirism_the_masquerade:textures/gui/sanguine.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        var vAtt = VampirismPlayerAttributes.get(mc.player).getVampSpecial();
        if (!vAtt.blood_vision) return;

        Entity entity = event.getEntity();
        if (!(entity instanceof Villager villager)) return;

        int humorId = HumorData.getHumor(villager);
        if (humorId < 0) return;

        ResourceLocation icon = switch (HumorType.byId(humorId)) {
            case PHLEGMATIC -> PHLEGMATIC_ICON;
            case CHOLERIC   -> CHOLERIC_ICON;
            case MELANCHOLIC-> MELANCHOLIC_ICON;
            case SANGUINE   -> SANGUINE_ICON;
        };

        PoseStack pose = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();

        pose.pushPose();
        pose.translate(0.0D, entity.getBbHeight() + 0.5D, 0.0D);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        pose.scale(0.02F, 0.02F, 0.02F);

        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(icon));
        PoseStack.Pose last = pose.last();
        int light = event.getPackedLight();
        float half = 16.0F;

        vc.addVertex(last, -half, -half, 0.0F).setColor(255, 255, 255, 255).setUv(0.0F, 0.0F).setOverlay(0).setLight(light).setNormal(last, 0.0F, 1.0F, 0.0F);
        vc.addVertex(last,  half, -half, 0.0F).setColor(255, 255, 255, 255).setUv(1.0F, 0.0F).setOverlay(0).setLight(light).setNormal(last, 0.0F, 1.0F, 0.0F);
        vc.addVertex(last,  half,  half, 0.0F).setColor(255, 255, 255, 255).setUv(1.0F, 1.0F).setOverlay(0).setLight(light).setNormal(last, 0.0F, 1.0F, 0.0F);
        vc.addVertex(last, -half,  half, 0.0F).setColor(255, 255, 255, 255).setUv(0.0F, 1.0F).setOverlay(0).setLight(light).setNormal(last, 0.0F, 1.0F, 0.0F);

        pose.popPose();
    }
}

