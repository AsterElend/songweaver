package aster.songweaver.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Set;

public class WardedBlockRenderer {

    private static final float BASE_ALPHA = 0.25f;

    public static void render(WorldRenderContext context) {

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if (world == null) return;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        VertexConsumer buffer = consumers.getBuffer(SongweaverRenderLayers.WARD_LAYER);

        Set<Long> warded = ClientWardedState.get();

        long time = world.getTime();

        matrices.push();

        for (long l : warded) {

            BlockPos pos = BlockPos.fromLong(l);

            // Distance culling (important)
            if (!pos.isWithinDistance(camPos, 64)) continue;

            // 🌈 Rainbow hue (time + per-block offset)
            float offset = (l % 1000) / 1000f;
            float hue = ((time % 200) / 200f + offset) % 1.0f;

            int rgb = Color.HSBtoRGB(hue, 0.8f, 1f);

            float r = ((rgb >> 16) & 255) / 255f;
            float g = ((rgb >> 8) & 255) / 255f;
            float b = (rgb & 255) / 255f;

            // ✨ Pulsing alpha
            float pulse = 0.1f * (float)Math.sin((time + l) * 0.05);
            float alpha = BASE_ALPHA + pulse;

            // 🌫 Distance fade
            double distSq = pos.getSquaredDistance(camPos);
            float fade = (float)Math.max(0.2, 1.0 - (distSq / (64 * 64)));
            float finalAlpha = alpha * fade;

            matrices.push();

            // Move to block relative to camera
            matrices.translate(
                    pos.getX() - camPos.x,
                    pos.getY() - camPos.y,
                    pos.getZ() - camPos.z
            );

            // 🔮 Subtle breathing scale
            float scale = 1.01f + 0.01f * (float)Math.sin((time + l) * 0.05);
            matrices.scale(scale, scale, scale);

            drawCube(matrices, buffer, r, g, b, finalAlpha);

            matrices.pop();
        }

        matrices.pop();
    }

    private static void drawCube(MatrixStack matrices, VertexConsumer buffer,
                                 float r, float g, float b, float a) {

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float min = -0.01f;
        float max = 1.01f;

        // FRONT
        quad(buffer, mat, min, min, max,  max, min, max,  max, max, max,  min, max, max, r,g,b,a);

        // BACK
        quad(buffer, mat, max, min, min,  min, min, min,  min, max, min,  max, max, min, r,g,b,a);

        // LEFT
        quad(buffer, mat, min, min, min,  min, min, max,  min, max, max,  min, max, min, r,g,b,a);

        // RIGHT
        quad(buffer, mat, max, min, max,  max, min, min,  max, max, min,  max, max, max, r,g,b,a);

        // TOP
        quad(buffer, mat, min, max, max,  max, max, max,  max, max, min,  min, max, min, r,g,b,a);

        // BOTTOM
        quad(buffer, mat, min, min, min,  max, min, min,  max, min, max,  min, min, max, r,g,b,a);
    }

    private static void quad(VertexConsumer buffer, Matrix4f mat,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float r, float g, float b, float a) {

        buffer.vertex(mat, x1,y1,z1).color(r,g,b,a).next();
        buffer.vertex(mat, x2,y2,z2).color(r,g,b,a).next();
        buffer.vertex(mat, x3,y3,z3).color(r,g,b,a).next();
        buffer.vertex(mat, x4,y4,z4).color(r,g,b,a).next();
    }
}
