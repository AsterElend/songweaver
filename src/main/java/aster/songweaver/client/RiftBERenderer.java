package aster.songweaver.client;

import aster.songweaver.registry.physical.be.RiftBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;


public class RiftBERenderer implements BlockEntityRenderer<RiftBlockEntity> {

    public static ShaderProgram SHADER;
    public RiftBERenderer(BlockEntityRendererFactory.Context ctx) {
    }



    @Override
    public void render(RiftBlockEntity entity,
                       float tickDelta,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       int overlay) {

        if (entity.getWorld() == null) return;

        // Ensure shader is bound


        matrices.push();

        // Center in block (important for symmetry)
        matrices.translate(0.5, 0.0, 0.5);
        MinecraftClient client = MinecraftClient.getInstance();
        matrices.multiply(client.getEntityRenderDispatcher().getRotation());

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Time uniform
        float time = entity.getWorld().getTime() + tickDelta;
        if (SHADER != null) {
            var timeUniform = SHADER.getUniform("Time");
            if (timeUniform != null) {
                timeUniform.set(time * 0.05f);
            }
        }

        VertexConsumer vc = vertexConsumers.getBuffer(SongweaverRenderLayers.RIFT_LAYER);

        // Slight forward offset to avoid z-fighting
        float z = 0.01f;

        // --- TRIANGLE 1 ---
        vc.vertex(matrix, -0.8f, 0.0f, z)
                .color(255, 255, 255, 255)
                .texture(0f, 0f)
                .next();

        vc.vertex(matrix, -0.8f, 1.6f, z)
                .color(255, 255, 255, 255)
                .texture(0f, 1f)
                .next();

        vc.vertex(matrix,  0.8f, 1.6f, z)
                .color(255, 255, 255, 255)
                .texture(1f, 1f)
                .next();

        // --- TRIANGLE 2 ---
        vc.vertex(matrix, -0.8f, 0.0f, z)
                .color(255, 255, 255, 255)
                .texture(0f, 0f)
                .next();

        vc.vertex(matrix,  0.8f, 1.6f, z)
                .color(255, 255, 255, 255)
                .texture(1f, 1f)
                .next();

        vc.vertex(matrix,  0.8f, 0.0f, z)
                .color(255, 255, 255, 255)
                .texture(1f, 0f)
                .next();

        matrices.pop();
    }

        @Override
        public boolean rendersOutsideBoundingBox(RiftBlockEntity blockEntity) {
            return true; // allows tall tear visuals
        }

    }

