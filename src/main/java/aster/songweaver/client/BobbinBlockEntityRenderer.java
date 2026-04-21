package aster.songweaver.client;


import aster.songweaver.registry.physical.be.BobbinBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)public class BobbinBlockEntityRenderer implements BlockEntityRenderer<BobbinBlockEntity> {
    // Pillar bounds (minimum wool size)
    private static final float PILLAR_MIN_X = 5f / 16f;
    private static final float PILLAR_MAX_X = 11f / 16f;
    private static final float PILLAR_MIN_Z = 5f / 16f;
    private static final float PILLAR_MAX_Z = 11f / 16f;

    // Base platform bounds (maximum wool size)
    private static final float BASE_MIN_X = 1f / 16f;
    private static final float BASE_MAX_X = 15f / 16f;
    private static final float BASE_MIN_Z = 1f / 16f;
    private static final float BASE_MAX_Z = 15f / 16f;

    // Wool Y bounds match the pillar height
    private static final float WOOL_MIN_Y = 2f / 16f;
    private static final float WOOL_MAX_Y = 16f / 16f;

    public BobbinBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BobbinBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        int count = entity.getCount();       // current number of wool stored
        int maxCount = entity.getMaxCount(); // maximum capacity (1 stack = 64)

        if (count <= 0) return;

        float t = (float) count / (float) maxCount; // 0.0 → 1.0 fill ratio

        // Lerp from pillar size to base platform size
        float minX = lerp(PILLAR_MIN_X, BASE_MIN_X, t);
        float maxX = lerp(PILLAR_MAX_X, BASE_MAX_X, t);
        float minZ = lerp(PILLAR_MIN_Z, BASE_MIN_Z, t);
        float maxZ = lerp(PILLAR_MAX_Z, BASE_MAX_Z, t);

        Block renderFetch = switch (entity.getColor()){
            case 0 -> Blocks.WHITE_WOOL;
            case 1 -> Blocks.ORANGE_WOOL;
            case 2 -> Blocks.MAGENTA_WOOL;
            case 3 -> Blocks.LIGHT_BLUE_WOOL;
            case 4 -> Blocks.YELLOW_WOOL;
            case 5 -> Blocks.LIME_WOOL;
            case 6 -> Blocks.PINK_WOOL;
            case 7 -> Blocks.GRAY_WOOL;
            case 8 -> Blocks.LIGHT_GRAY_WOOL;
            case 9 -> Blocks.CYAN_WOOL;
            case 10 -> Blocks.PURPLE_WOOL;
            case 11 -> Blocks.BLUE_WOOL;
            case 12 -> Blocks.BROWN_WOOL;
            case 13 -> Blocks.GREEN_WOOL;
            case 14 -> Blocks.RED_WOOL;
            case 15 -> Blocks.BLACK_WOOL;
            default -> Blocks.WHITE_WOOL;
        };


        // Get white wool sprite from the block atlas

        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        Sprite sprite = blockRenderManager.getModels()
                .getModelParticleSprite(renderFetch.getDefaultState());

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getCutoutMipped());

        matrices.push();

        renderCuboid(matrices, consumer, minX, WOOL_MIN_Y, minZ, maxX, WOOL_MAX_Y, maxZ, sprite, light, overlay);

        matrices.pop();
        ItemStack stack = entity.getStack();

        if (!stack.isEmpty()){
            matrices.push();
            float time = entity.getWorld().getTime() % 50000 + tickDelta;
            matrices.translate(0.5, 1.5, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 2));
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light, overlay,
                    matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }


    }

    /**
     * Renders all 6 faces of an axis-aligned cuboid using the given sprite.
     */
    private void renderCuboid(MatrixStack matrices, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              Sprite sprite, int light, int overlay) {

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f model = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        // U/V extents from the atlas sprite
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();

        int r = 255, g = 255, b = 255, a = 255;

        // ----- DOWN face (y1, normal 0,-1,0) -----
        consumer.vertex(model, x1, y1, z1).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,-1,0).next();
        consumer.vertex(model, x2, y1, z1).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,-1,0).next();
        consumer.vertex(model, x2, y1, z2).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,-1,0).next();
        consumer.vertex(model, x1, y1, z2).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,-1,0).next();

        // ----- UP face (y2, normal 0,+1,0) -----
        consumer.vertex(model, x1, y2, z2).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,1,0).next();
        consumer.vertex(model, x2, y2, z2).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,1,0).next();
        consumer.vertex(model, x2, y2, z1).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,1,0).next();
        consumer.vertex(model, x1, y2, z1).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,1,0).next();

        // ----- NORTH face (z1, normal 0,0,-1) -----
        consumer.vertex(model, x2, y1, z1).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x1, y1, z1).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x1, y2, z1).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x2, y2, z1).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,0,-1).next();

        // ----- SOUTH face (z2, normal 0,0,+1) -----
        consumer.vertex(model, x1, y1, z2).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x2, y1, z2).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x2, y2, z2).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x1, y2, z2).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,0,1).next();

        // ----- WEST face (x1, normal -1,0,0) -----
        consumer.vertex(model, x1, y1, z2).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y1, z1).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y2, z1).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y2, z2).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, -1,0,0).next();

        // ----- EAST face (x2, normal +1,0,0) -----
        consumer.vertex(model, x2, y1, z1).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y1, z2).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y2, z2).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y2, z1).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 1,0,0).next();

        // ----- NORTH face (z1, normal 0,0,-1) -----
// Viewed from outside (from -Z looking +Z), CCW order
        consumer.vertex(model, x1, y1, z1).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x2, y1, z1).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x2, y2, z1).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,0,-1).next();
        consumer.vertex(model, x1, y2, z1).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,0,-1).next();

// ----- SOUTH face (z2, normal 0,0,+1) -----
// Viewed from outside (from +Z looking -Z), CCW order
        consumer.vertex(model, x2, y1, z2).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x1, y1, z2).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x1, y2, z2).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 0,0,1).next();
        consumer.vertex(model, x2, y2, z2).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 0,0,1).next();

// ----- WEST face (x1, normal -1,0,0) -----
// Viewed from outside (from -X looking +X), CCW order
        consumer.vertex(model, x1, y1, z1).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y1, z2).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y2, z2).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, -1,0,0).next();
        consumer.vertex(model, x1, y2, z1).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, -1,0,0).next();

// ----- EAST face (x2, normal +1,0,0) -----
// Viewed from outside (from +X looking -X), CCW order
        consumer.vertex(model, x2, y1, z2).color(r,g,b,a).texture(u0,v1).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y1, z1).color(r,g,b,a).texture(u1,v1).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y2, z1).color(r,g,b,a).texture(u1,v0).overlay(overlay).light(light).normal(normal, 1,0,0).next();
        consumer.vertex(model, x2, y2, z2).color(r,g,b,a).texture(u0,v0).overlay(overlay).light(light).normal(normal, 1,0,0).next();
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}