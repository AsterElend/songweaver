package aster.songweaver.client;

import aster.songweaver.registry.physical.ritual.KhipuHookBlock;
import aster.songweaver.registry.physical.ritual.KhipuHookBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class KhipuHookBlockEntityRenderer
        implements BlockEntityRenderer<KhipuHookBlockEntity> {

    public KhipuHookBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(KhipuHookBlockEntity hook,
                       float tickDelta,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       int overlay) {

        if (!hook.hasItem()) return;

        ItemStack stack = hook.getStack(0);

        matrices.push();

        // Center of block
        matrices.translate(0.5, 0.5, 0.5);

        // Rotate to match wall facing
        Direction facing = hook.getCachedState()
                .get(KhipuHookBlock.FACING);

        matrices.multiply(RotationAxis.POSITIVE_Y
                .rotationDegrees(-facing.asRotation()));

        // Offset slightly away from wall
        matrices.translate(0.0, -0.1, -0.35);

        // Scale up for decoration
        float scale = 1.4f;
        matrices.scale(scale, scale, scale);

        MinecraftClient.getInstance()
                .getItemRenderer()
                .renderItem(
                        stack,
                        ModelTransformationMode.FIXED,
                        light,
                        overlay,
                        matrices,
                        vertexConsumers,
                        hook.getWorld(),
                        0
                );

        matrices.pop();
    }
}
