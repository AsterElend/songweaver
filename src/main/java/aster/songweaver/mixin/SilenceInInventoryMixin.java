package aster.songweaver.mixin;

import aster.songweaver.cca.SilenceComponent;
import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.LoomItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(AbstractInventoryScreen.class)
public abstract class SilenceInInventoryMixin<T extends ScreenHandler> extends HandledScreen<T> {

    public SilenceInInventoryMixin(T screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawStatusEffects", at = @At("HEAD"))
    private void renderSilenceInInventory(DrawContext ctx, int mouseX, int mouseY, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        SilenceComponent component = SongweaverComponents.SILENCE.get(client.player);
        if (!component.isSilenced()) return;

        // Count existing beneficial statuses to stack below them
        int beneficialCount = 0;
        for (StatusEffectInstance e : client.player.getStatusEffects()) {
            if (e.shouldShowIcon() && e.getEffectType().isBeneficial()) beneficialCount++;
        }

        // Vanilla renders the effect background at x - 124, icons inside that
        // Each effect slot is 24px tall with 1px gap = 25px
        int effectsX = x - 120; // x is HandledScreen's panel left edge
        int effectsY = y + 1 + (beneficialCount * 25);

        ctx.drawTexture(HandledScreen.BACKGROUND_TEXTURE, effectsX, effectsY, 141, 166, 24, 24);
        ctx.drawItem(new ItemStack(LoomItems.THREAD), effectsX + 3, effectsY + 3);
        ctx.drawText(client.textRenderer, durationToString(component.getSilenceDuration()),
                effectsX + 3, effectsY + 13, 0xFFFFFF, true);
    }

    @Unique
    private static String durationToString(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return minutes > 99 ? Integer.toString(minutes) : String.format("%d:%02d", minutes, seconds);
    }
}