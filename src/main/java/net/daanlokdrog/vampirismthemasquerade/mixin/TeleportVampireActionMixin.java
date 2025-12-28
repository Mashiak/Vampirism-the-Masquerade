package net.daanlokdrog.vampirismthemasquerade.mixin;

import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import de.teamlapen.vampirism.entity.player.vampire.actions.TeleportVampireAction;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Just for immersion. I don't understand why the original mod didn't have this.
 */

@Mixin(TeleportVampireAction.class)
public abstract class TeleportVampireActionMixin {

    @Redirect(
        method = "activate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
private boolean replaceCloud(Level level, Entity entity) {
       if (entity instanceof AreaParticleCloudEntity cloud && level instanceof ServerLevel serverLevel) {
            double ox = cloud.getX();
            double oy = cloud.getY() + cloud.getBbHeight() / 2.0;
            double oz = cloud.getZ();
            spawnBatParticles(serverLevel, ox, oy, oz);

            Entity player = serverLevel.getNearestPlayer(ox, oy, oz, 100, false);
       if (player != null) {
                double nx = player.getX();
                double ny = player.getY() + player.getBbHeight() / 2.0;
                double nz = player.getZ();
                spawnBatParticles(serverLevel, nx, ny, nz);

                serverLevel.playSound(
                    null,
                    nx, ny, nz,
                    SoundEvents.BAT_AMBIENT,
                    SoundSource.PLAYERS,
                    1.5f,
                    1.0f
                );
            }

        return false;
        }
        return level.addFreshEntity(entity);
    }

private void spawnBatParticles(ServerLevel serverLevel, double cx, double cy, double cz) {
     int count = 8;
for (int i = 0; i < count; i++) {
        double angle = serverLevel.random.nextDouble() * 2 * Math.PI;
        double speed = 0.4 + serverLevel.random.nextDouble() * 0.4;
        double dx = Math.cos(angle) * speed;
        double dz = Math.sin(angle) * speed;
        double dy = (serverLevel.random.nextDouble() - 0.5) * 0.5;

        double py = cy;

        serverLevel.sendParticles(
            VampirismTheMasqueradeModParticleTypes.BAT_PARTICLE.get(),
            cx, py, cz,
            1,
            dx, dy, dz,
            0.0
        );
    }
}
}

