package aster.songweaver.registry;

import aster.songweaver.client.SilkParticle;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SongweaverParticles {

    public static final DefaultParticleType SILK_PARTICLE =
            FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE,
                new Identifier("songweaver", "silk_particle"),
                SILK_PARTICLE);
    }



    public static class SilkParticleFactory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public SilkParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {

            SilkParticle particle = new SilkParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(spriteProvider);

            return particle;
        }
    }
}
