/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.daanlokdrog.vampirismthemasquerade.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.daanlokdrog.vampirismthemasquerade.client.particle.BlueClothParticle;
import net.daanlokdrog.vampirismthemasquerade.client.particle.BatParticleParticle;

@EventBusSubscriber(Dist.CLIENT)
public class VampirismTheMasqueradeModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(VampirismTheMasqueradeModParticleTypes.BAT_PARTICLE.get(), BatParticleParticle::provider);
		event.registerSpriteSet(VampirismTheMasqueradeModParticleTypes.BLUE_CLOTH.get(), BlueClothParticle::provider);
	}
}