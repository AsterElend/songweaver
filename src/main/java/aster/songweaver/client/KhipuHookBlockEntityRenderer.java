package aster.songweaver.client;

import aster.songweaver.api.weaving.Note;
import aster.songweaver.registry.physical.be.MusicStandBlockEntity;
import aster.songweaver.registry.physical.block.KhipuHookBlock;
import aster.songweaver.registry.physical.be.KhipuHookBlockEntity;
import aster.songweaver.registry.physical.block.MusicStandBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.List;

@Environment(EnvType.CLIENT)
public class KhipuHookBlockEntityRenderer
        implements BlockEntityRenderer<KhipuHookBlockEntity> {

    private final ItemRenderer itemRenderer;

    public KhipuHookBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(
            KhipuHookBlockEntity stand,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        ItemStack stack = stand.getStack(0);
        if (stack.isEmpty()) return;

        World world = stand.getWorld();
        if (world == null) return;

        BlockState state = stand.getCachedState();
        Direction facing = state.get(MusicStandBlock.FACING);

        matrices.push();

        // === 1. Move to block center ===
        matrices.translate(0.5, -0.4, 1);

        // === 2. Rotate to match block facing ===
        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation())
        );

        // === 3. Move item onto the music plate (center of shelf) ===
        // Based on your Blockbench model: shelf is roughly Y = 1.30, Z = 0.35
        matrices.translate(0.0, .5, 0.05);


        // === 5. Scale item down to fit ===
        matrices.scale(0.75f, 0.75f, 0.75f);

        // === 6. Render the item ===
        int seed = (int) stand.getPos().asLong();
        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                world,
                seed
        );

        matrices.pop();

        renderPos(stand, facing, matrices, vertexConsumers, light);
    }

    private void renderPos(
            KhipuHookBlockEntity stand,
            Direction facing,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light
    ) {
        if (!stand.hasStoredLocation()) return;
        BlockPos pos = stand.getStoredPos();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float time = stand.getWorld().getTime() + MinecraftClient.getInstance().getTickDelta();

        String text = String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());

        matrices.push();

        matrices.translate(0.5, 1.6, 0.5);
        matrices.translate(0.0, MathHelper.sin(time * 0.05f) * 0.05f, 0.0);
        matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        textRenderer.draw(
                text,
                -textRenderer.getWidth(text) / 2f, // center it
                0,
                0x80D8FF,
                false,
                matrices.peek().getPositionMatrix(),
                vertices,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                light
        );

        matrices.pop();
    }
    @Override
    public boolean rendersOutsideBoundingBox(KhipuHookBlockEntity blockEntity) {
        return false;
    }


}
