package aster.songweaver.mixin;

import aster.songweaver.cca.SongweaverComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
@Environment(EnvType.CLIENT)
@Mixin(AdvancementWidget.class)
public class ForgottenAdvancementsScreen {

    @Final
    @Shadow
    private MinecraftClient client;

    @Final
    @Shadow
    private Advancement advancement;

    @Final
    @Shadow private AdvancementDisplay display; // has the icon, frame type, etc.

    @Final
    @Shadow private List<AdvancementWidget> children;
    @Shadow @Nullable
    private AdvancementProgress progress;

    // The texture atlas used for advancement frames
    @Shadow
    @Final
    private static Identifier WIDGETS_TEXTURE;

    @Final
    @Shadow
    private int x;

    @Final
    @Shadow
    private int y;

    @Inject(method = "renderWidgets", at = @At("HEAD"), cancellable = true)
    private void lethean$greyOutWidget(DrawContext context, int x, int y, CallbackInfo ci) {
        if (client == null || client.player == null) return;
        if (!SongweaverComponents.FORGOTTEN.get(client.player).isForgotten(advancement.getId())) return;

        ci.cancel();
        AdvancementObtainedStatus advancementObtainedStatus;

        // Draw the frame greyed out, mirroring the original logic
        if (this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
            if (f >= 1.0F) {
                advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            } else {
                advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            }


            RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1.0f);
            context.drawTexture(WIDGETS_TEXTURE, x + this.x + 3, y + this.y, this.display.getFrame().getTextureV(), 128 + advancementObtainedStatus.getSpriteIndex() * 26, 26, 26);
            context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // reset immediately
        }

        // Still recurse into children
        for (AdvancementWidget child : children) {
            child.renderWidgets(context, x, y);
        }
    }
    @Inject(method = "drawTooltip", at = @At("HEAD"), cancellable = true)
    private void lethean$overrideTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        if (client == null || client.player == null ) return;

        Identifier id = advancement.getId();

        if (SongweaverComponents.FORGOTTEN.get(client.player).isForgotten(id)) {
            ci.cancel();

            context.drawTooltip(
                    client.textRenderer,
                    List.of(
                            Text.literal("An Absence").formatted(Formatting.GRAY),
                            Text.literal("There used to be something here.").formatted(Formatting.DARK_GRAY)
                    ),
                    Optional.empty(),
                    x, y
            );


        }
    }


}
