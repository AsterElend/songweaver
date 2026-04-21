package aster.songweaver.mixin;

import aster.songweaver.cca.SilenceComponent;
import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.LoomItems;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(InGameHud.class)
public class SilenceInHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledWidth;

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void renderSilenceCountNoEffects(DrawContext ctx, CallbackInfo ci) {
        if (client.player == null) return;
        if (!client.player.getStatusEffects().isEmpty()) return; // let TAIL handle it

        SilenceComponent component = SongweaverComponents.SILENCE.get(client.player);
        if (!component.isSilenced()) return;

        int k = scaledWidth - 25;
        int l = client.isDemo() ? 16 : 1;

        ctx.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 141, 166, 24, 24);
        ctx.drawItem(new ItemStack(LoomItems.THREAD), k + 3, l + 3);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
    private void renderSilenceCount(DrawContext ctx, CallbackInfo ci,
                                    @Local Collection<StatusEffectInstance> collection) {
        if (collection == null) return;
        if (client.player == null) return;

        SilenceComponent component = SongweaverComponents.SILENCE.get(client.player);
        if (!component.isSilenced()) return;

        int beneficialCount = 0;
        for (StatusEffectInstance effect : collection) {
            if (effect.shouldShowIcon() && effect.getEffectType().isBeneficial()) {
                beneficialCount++;
            }
        }

        int k = scaledWidth - 25 * (beneficialCount + 1);
        int l = client.isDemo() ? 16 : 1;

        ctx.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 141, 166, 24, 24);
        ctx.drawItem(new ItemStack(LoomItems.THREAD), k + 3, l + 3);

    }

}
