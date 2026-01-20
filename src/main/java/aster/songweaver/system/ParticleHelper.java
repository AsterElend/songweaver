package aster.songweaver.system;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class ParticleHelper {
    public static void spawnParticleBurst(
            ServerWorld world,
            BlockPos pos,
            ParticleEffect particle,
            int count,
            double speed
    ) {
        Vec3d center = Vec3d.ofCenter(pos);
        Random random = world.getRandom();

        for (int i = 0; i < count; i++) {
            double vx = (random.nextDouble() - 0.5) * speed;
            double vy = (random.nextDouble() - 0.5) * speed;
            double vz = (random.nextDouble() - 0.5) * speed;

            world.spawnParticles(
                    particle,
                    center.x,
                    center.y,
                    center.z,
                    1,
                    vx,
                    vy,
                    vz,
                    0.0
            );
        }
    }

    public static void spawnParticleWave(
            ServerWorld world,
            BlockPos pos,
            ParticleEffect particle,
            double maxRadius,
            int particlesPerRing
    ) {
        Vec3d center = Vec3d.ofCenter(pos);
        Random random = world.getRandom();

        for (double radius = 0.5; radius <= maxRadius; radius += 0.5) {
            for (int i = 0; i < particlesPerRing; i++) {

                // Random direction on a sphere
                double theta = random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(2 * random.nextDouble() - 1);

                double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
                double y = center.y + radius * Math.cos(phi);
                double z = center.z + radius * Math.sin(phi) * Math.sin(theta);

                world.spawnParticles(
                        particle,
                        x,
                        y,
                        z,
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }
    }

    public static void spawnRandomNoteParticle(ServerWorld world, BlockPos pos) {

        final Random RANDOM = Random.create();

        float note = RANDOM.nextFloat(); // 0.0–1.0 = random color

        world.spawnParticles(
                ParticleTypes.NOTE,
                pos.getX() + 0.5,
                pos.getY() + 1.1,
                pos.getZ() + 0.5,
                1,          // count
                0.0, 0.0, 0.0, // offset (ignored for NOTE)
                note        // this controls the color
        );
    }



}
