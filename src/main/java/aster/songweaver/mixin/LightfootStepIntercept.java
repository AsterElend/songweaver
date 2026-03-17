package aster.songweaver.mixin;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class LightfootStepIntercept {
    @Inject(
            method = "onSteppedOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventStep(
            World world,
            BlockPos pos,
            BlockState state,
            Entity entity,
            CallbackInfo ci
    ) {
        if (entity instanceof LivingEntity living) {

            if (living.hasStatusEffect(LoomMiscRegistry.LIGHTFOOT)) {
                ci.cancel();
            }

        }
    }
}

