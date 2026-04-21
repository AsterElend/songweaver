package aster.songweaver.client;

import aster.songweaver.api.renderNonsense.SiphonRenderState;
import aster.songweaver.api.renderNonsense.TravelingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class SiphonRenderer {



    public static void render(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null) return;

        long currentTick = world.getTime();
        float partialTick = ctx.tickDelta();

        Camera camera = ctx.camera();
        Vec3d camPos = camera.getPos();

        MatrixStack matrices = ctx.matrixStack();
        VertexConsumerProvider.Immediate immediate =
                client.getBufferBuilders().getEntityVertexConsumers();

        // --- Draw lines ---
        VertexConsumer lineConsumer = immediate.getBuffer(RenderLayer.LINES);
        for (SiphonRenderState.ActiveLine line : SiphonRenderState.getActiveLines()) {
            drawLine(matrices, lineConsumer, camPos, line);
        }
        immediate.draw(RenderLayer.LINES);

        // --- Draw traveling items ---
        for (TravelingItem item : SiphonRenderState.getTravelingItems()) {
            double t = item.getT(currentTick, partialTick);
            Vec3d pos = item.start().lerp(item.end(), t);

            // Bob slightly perpendicular to path for visual separation
            double bob = Math.sin(t * Math.PI) * 0.3; // arc upward mid-travel

            matrices.push();
            matrices.translate(
                    pos.x - camPos.x,
                    pos.y - camPos.y + bob,
                    pos.z - camPos.z
            );

            // Face the camera
            matrices.multiply(camera.getRotation());

            // Scale down — items are 1 block by default
            float scale = 0.4f;
            matrices.scale(scale, scale, scale);

            client.getItemRenderer().renderItem(
                    item.stack(),
                    ModelTransformationMode.GROUND,
                    15728880, // full-bright
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    immediate,
                    world,
                    0
            );

            matrices.pop();
            immediate.draw();
        }
    }

    private static void drawLine(MatrixStack matrices, VertexConsumer consumer,
                                 Vec3d camPos, SiphonRenderState.ActiveLine line) {
        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        Vec3d s = line.start();
        Vec3d e = line.end();
        float r = line.color().x;
        float g = line.color().y;
        float b = line.color().z;
        float alpha = 0.6f;

        // Compute normal for the line (required by RenderLayer.LINES)
        Vec3d dir = e.subtract(s).normalize();
        float nx = (float) dir.x;
        float ny = (float) dir.y;
        float nz = (float) dir.z;

        consumer.vertex(matrix, (float)s.x, (float)s.y, (float)s.z)
                .color(r, g, b, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();
        consumer.vertex(matrix, (float)e.x, (float)e.y, (float)e.z)
                .color(r, g, b, alpha)
                .normal(normalMatrix, nx, ny, nz)
                .next();

        matrices.pop();
    }
}
