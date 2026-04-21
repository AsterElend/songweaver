package aster.songweaver.mixin;

import aster.songweaver.api.TouchingWaterAware;
import aster.songweaver.registry.LoomTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ActuallySwimCheck {
    @WrapOperation(method = {"updateMovementInFluid", "updateSwimming"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean spectrum$updateMovementInFluid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        if (original.call(instance, tag)) return true;
        return tag == FluidTags.WATER ? original.call(instance, LoomTags.LETHE) : false;
    }

    // Used to cache the state being submerged in water, which is used for initiating swimming.

    @WrapOperation(method = "updateSubmergedInWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean spectrum$updateSubmergedInWaterState(Entity instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        if (original.call(instance, tag)) return true;
        return tag == FluidTags.WATER ? original.call(instance, LoomTags.LETHE) : false;
    }



    @Inject(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"))
    public void spectrum$updateMovementInFluid(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> info, @Local FluidState fluidState) {
        ((TouchingWaterAware) this).spectrum$setActuallyTouchingWater(fluidState.isIn(FluidTags.WATER));
    }
}
