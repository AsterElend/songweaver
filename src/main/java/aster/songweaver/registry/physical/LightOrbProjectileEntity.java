package aster.songweaver.registry.physical;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightOrbProjectileEntity extends ThrownEntity {

    public LightOrbProjectileEntity(EntityType<? extends LightOrbProjectileEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.getWorld().isClient && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos placePos = blockHit.getBlockPos().offset(blockHit.getSide());

            // Only place if air
            if (this.getWorld().isAir(placePos)) {
                this.getWorld().setBlockState(placePos, LoomMiscRegistry.LIGHT_ORB.getDefaultState(), 3);
            }

            this.remove(RemovalReason.KILLED);
        }

        super.onCollision(hitResult);
    }

    @Override
    protected void initDataTracker() {
        // No custom tracked data
    }

    @Override
    protected float getGravity() {
        return 0.0f; // No gravity, orb flies straight
    }
}