package aster.songweaver.mixin;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class UnravelingClearPrevention {

    @Inject(
            method = "clearStatusEffects",
            at = @At("HEAD")
    )
    private void preventUnravelingClear(CallbackInfoReturnable<Boolean> cir) {

        LivingEntity self = (LivingEntity)(Object)this;

        if (self.hasStatusEffect(LoomMiscRegistry.UNRAVELING) && !SongweaverComponents.UNRAVELING_STATE.get(self).isClearable()) {

            StatusEffectInstance inst = self.getStatusEffect(LoomMiscRegistry.UNRAVELING);

            self.clearStatusEffects();


            if (inst != null) {
                self.addStatusEffect(inst);
            }

        }
    }
}
