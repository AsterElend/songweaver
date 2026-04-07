package aster.songweaver.mixin;

import aster.songweaver.api.WardedBlocksState;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExplosionBehavior.class)
public class ExplosionWard {


    @Inject(method = "getBlastResistance", at = @At("HEAD"), cancellable = true)
    private void removeWardedBlocks(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, CallbackInfoReturnable<Optional<Float>> cir) {

        // ✅ only run on server
        if (!(world instanceof ServerWorld serverWorld)) return;

        WardedBlocksState ward = WardedBlocksState.get(serverWorld);
        if (ward.isWarded(pos, blockState)){
            cir.setReturnValue(Optional.of(Float.MAX_VALUE));

        }

    }
}


