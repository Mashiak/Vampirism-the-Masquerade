package net.daanlokdrog.vampirismthemasquerade.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class LargeCampfireSmokeParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected LargeCampfireSmokeParticle(ClientLevel level, double x, double y, double z,
                                         double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, x, y, z, dx, dy, dz);
        this.sprites = spriteSet;
        this.setSpriteFromAge(this.sprites);
        this.gravity = 0.0F;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.quadSize *= 6.5F;
        this.lifetime = 200 + this.random.nextInt(20);
    }

    @Override
    public void tick() {
        super.tick();
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new LargeCampfireSmokeParticle(level, x, y, z, 0.0, 0.0, 0.0, this.spriteSet);
        }
    }
}
