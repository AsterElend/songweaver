package aster.songweaver.client;

import aster.songweaver.system.definition.Note;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.util.stream.Collectors;

public class SongHud {

    public static void init() {
        HudRenderCallback.EVENT.register(SongHud::render);
    }

    private static void render(DrawContext ctx, float tickDelta) {
        if (DistaffHelper.isNotHoldingDistaff()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String text = InputBuffer.snapshot().stream()
                .map(Note::display)
                .collect(Collectors.joining(" – "));

        ctx.drawText(
                client.textRenderer,
                Formatting.AQUA + "♫ " + text + " ♫",
                10,
                client.getWindow().getScaledHeight() - 30,
                0xFFFFFF,
                true
        );
    }
}
