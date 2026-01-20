package aster.songweaver.client;


import aster.songweaver.Songweaver;
import aster.songweaver.system.ritual.BobbinBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BobbinBlockEntityRenderer implements BlockEntityRenderer<BobbinBlockEntity> {

    private static final ItemRenderer RENDERER = MinecraftClient.getInstance().getItemRenderer();


    public BobbinBlockEntityRenderer(BlockEntityRendererFactory.Context renderContext) {

    }

    @Override
    public void render(
            BobbinBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {



        ItemStack stack = entity.getStack();
        if (stack.isEmpty()) return;

        int renderCount = getRenderCount(stack);
        long time = entity.getWorld().getTime() % 36000;
        float speed = 0.03f ;
        float baseAngle = (time + tickDelta) * speed;
        float radius = 0.8F;






        for (int i = 0; i < renderCount; i++) {
            float angle = baseAngle + (float)(2 * Math.PI * i / renderCount);


            float x = MathHelper.cos(angle) * radius;
            float z = MathHelper.sin(angle) * radius;

            float phase = i * 1.3f;


            matrices.push();

            matrices.translate(0.5 + x, 0.5, 0.5 + z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(angle));
            matrices.scale(0.5f, 0.5f, 0.5f);

            RENDERER.renderItem(
                            stack,
                            ModelTransformationMode.GROUND,
                            light,
                            overlay,
                            matrices,
                            vertexConsumers,
                            entity.getWorld(),
                            i
                    );

            matrices.pop();
        }
    }

    private static int getRenderCount(ItemStack stack) {
        return stack.getCount();


       /* int count = stack.getCount();

        if (count > 48) return 5;
        if (count > 32) return 4;
        if (count > 16) return 3;
        if (count > 1)  return 2;
        return 1;*/
    }

}
