package aster.songweaver.mixin;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class LightfootTrampleIntercept {

    @Inject(
            method = "onLandedUpon",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventTrample(
            World world,
            BlockState state,
            BlockPos pos,
            Entity entity,
            float distance,
            CallbackInfo ci
    ){
        if (entity instanceof LivingEntity living){
            if (!living.hasStatusEffect(LoomMiscRegistry.LIGHTFOOT)) return;
            ci.cancel();
        }
    }
}
