package aster.songweaver.mixin;

import aster.songweaver.cca.ForgottenAdvancementComponent;
import aster.songweaver.cca.SongweaverComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    PlayerEntity player = client.player;

    @Inject(method = "drawTooltip", at = @At("HEAD"), cancellable = true)
    private void lethean$overrideTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        if (player == null) return;

        Identifier id = advancement.getId();

        if (SongweaverComponents.FORGOTTEN.get(player).isForgotten(id)) {
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
