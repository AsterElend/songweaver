package aster.songweaver.client;

import aster.songweaver.registry.physical.LoomBlockStuff;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SilkParticle extends SpriteBillboardParticle {

    public SilkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ){
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.gravityStrength = 0.25f; // ✅ gravity here
        this.velocityMultiplier = 0.98f;
        this.scale = 0.05f + random.nextFloat() * 0.1f;

        this.maxAge = 40;
        this.collidesWithWorld = true;
    }

    @Override
    public void tick() {
        Vec3d prevPos = new Vec3d(this.x, this.y, this.z);

        super.tick();

        if (this.dead) return;

        BlockPos currentPos = BlockPos.ofFloored(this.x, this.y, this.z);
        BlockState state = world.getBlockState(currentPos);

        // If we hit something solid AND it's not the rift block → kill particle
        if (!state.isAir() && !state.isOf(LoomBlockStuff.RIFT_BLOCK)) {
            this.markDead();
        } else {
            // allow movement (undo collision stop if needed)
            this.onGround = false;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }


}
