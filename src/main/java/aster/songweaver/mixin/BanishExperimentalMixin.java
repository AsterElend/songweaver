package aster.songweaver.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExperimentalWarningScreen.class, priority = 2000)
public abstract class BanishExperimentalMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void autoConfirm(CallbackInfo ci) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()){
        MinecraftClient.getInstance().setScreen(null);}
    }
}
