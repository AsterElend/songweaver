package aster.songweaver.mixin;

import aster.songweaver.system.definition.Note;
import aster.songweaver.client.DistaffHelper;
import aster.songweaver.client.InputBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class DistaffIntercept {
    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void songweaver$interceptHotbar(CallbackInfoReturnable<Boolean> cir){
        if (!cir.getReturnValue()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (DistaffHelper.isNotHoldingDistaff()) return;

        KeyBinding self = (KeyBinding)(Object)this;

        KeyBinding[] hotbar = client.options.hotbarKeys;
        for (int i = 0; i < hotbar.length; i++) {
            if (self == hotbar[i]) {

                Note note = Note.fromHotbarIndex(i);
                if (note != null) {
                    InputBuffer.add(note);
                    DistaffHelper.playNote(client, note);



                }

                // Prevent vanilla hotbar switching
                cir.setReturnValue(false);
                return;
            }
        }


    }

}


