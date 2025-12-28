package net.daanlokdrog.vampirismthemasquerade.util;

import net.daanlokdrog.vampirismthemasquerade.entity.VampireEvokerEntity;
import net.daanlokdrog.vampirismthemasquerade.init.VampirismTheMasqueradeModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class VampireIllagerTeleportUtil {

    private static final long TELEPORT_COOLDOWN = 30_000L;

    public static boolean tryTeleportNearTarget(Mob mob, LivingEntity target, long lastTeleportTime) {
        if (!canTeleport(lastTeleportTime)) return false;

        RandomSource random = mob.getRandom();
        double tx = target.getX() + (random.nextDouble() - 0.5) * 20.0;
        double ty = target.getY();
        double tz = target.getZ() + (random.nextDouble() - 0.5) * 20.0;

        BlockPos pos = BlockPos.containing(tx, ty, tz);

        if (mob.level().getBlockState(pos.below()).isSolid() && mob.level() instanceof ServerLevel serverLevel) {
            spawnBatParticles(serverLevel, mob.getX(), mob.getY() + mob.getBbHeight() / 2.0, mob.getZ());
            serverLevel.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    SoundEvents.BAT_AMBIENT, SoundSource.HOSTILE, 1.5f, 1.0f);
            serverLevel.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);

            mob.teleportTo(tx, ty, tz);

            spawnBatParticles(serverLevel, tx, ty + mob.getBbHeight() / 2.0, tz);
            serverLevel.playSound(null, tx, ty, tz,
                    SoundEvents.BAT_AMBIENT, SoundSource.HOSTILE, 1.5f, 1.0f);
            serverLevel.playSound(null, tx, ty, tz,
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);

            if (mob instanceof VampireEvokerEntity && random.nextFloat() < 0.5F) {
                performFangsAttack(serverLevel, mob);
            }

            return true;
        }
        return false;
    }

    private static boolean canTeleport(long lastTeleportTime) {
        return (System.currentTimeMillis() - lastTeleportTime) >= TELEPORT_COOLDOWN;
    }

    private static void spawnBatParticles(ServerLevel serverLevel, double cx, double cy, double cz) {
        int count = 8;
        for (int i = 0; i < count; i++) {
            double angle = serverLevel.random.nextDouble() * 2 * Math.PI;
            double speed = 0.4 + serverLevel.random.nextDouble() * 0.4;
            double dx = Math.cos(angle) * speed;
            double dz = Math.sin(angle) * speed;
            double dy = (serverLevel.random.nextDouble() - 0.5) * 0.5;
            serverLevel.sendParticles(
                VampirismTheMasqueradeModParticleTypes.BAT_PARTICLE.get(),
                cx, cy, cz,
                1,
                dx, dy, dz,
                0.0
            );
        }
    }

    private static void performFangsAttack(ServerLevel serverLevel, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (target != null) {
            double dx = target.getX() - mob.getX();
            double dz = target.getZ() - mob.getZ();
            float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
            mob.setYRot(yaw);
            mob.yHeadRot = yaw;
        }

        double dirX = -Math.sin(mob.getYRot() * (Math.PI / 180));
        double dirZ = Math.cos(mob.getYRot() * (Math.PI / 180));

        int count = 12;
        double step = 1.25;

        for (int i = 1; i <= count; i++) {
            double px = mob.getX() + dirX * (step * i);
            double pz = mob.getZ() + dirZ * (step * i);
            double topY = mob.getY() + 1.0;

            BlockPos place = BlockPos.containing(px, topY, pz);
            boolean foundGround = false;
            double yOffset = 0.0;

            for (int down = 0; down <= 6; down++) {
                BlockPos below = place.below();
                BlockState belowState = serverLevel.getBlockState(below);
                if (belowState.isFaceSturdy(serverLevel, below, Direction.UP)) {
                    if (!serverLevel.isEmptyBlock(place)) {
                        BlockState stateAtPlace = serverLevel.getBlockState(place);
                        var shape = stateAtPlace.getCollisionShape(serverLevel, place);
                        if (!shape.isEmpty()) {
                            yOffset = shape.max(Direction.Axis.Y);
                        }
                    }
                    foundGround = true;
                    break;
                }
                place = below;
            }

            if (foundGround) {
                double finalY = place.getY() + yOffset;
                EvokerFangs fangs = new EvokerFangs(serverLevel, px, finalY, pz, (float) mob.getYRot(), i - 1, mob);
                serverLevel.addFreshEntity(fangs);
                serverLevel.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE,
                        new Vec3(px, finalY, pz),
                        net.minecraft.world.level.gameevent.GameEvent.Context.of(mob));
            }
        }

        serverLevel.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.0f, 1.0f);
    }
}

