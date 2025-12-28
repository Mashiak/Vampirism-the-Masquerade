package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.daanlokdrog.vampirismthemasquerade.client.particle.LargeCampfireSmokeParticle;

@EventBusSubscriber(Dist.CLIENT)
public class LargeCampfireSmokeParticlesClient {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
            LargeCampfireSmokeParticles.LARGE_CAMPFIRE_SMOKE.get(),
            spriteSet -> new LargeCampfireSmokeParticle.Provider(spriteSet)
        );
    }
}
