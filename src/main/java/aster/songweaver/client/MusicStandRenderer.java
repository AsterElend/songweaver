package aster.songweaver.client;

import aster.songweaver.registry.physical.ritual.MusicStandBlock;
import aster.songweaver.registry.physical.ritual.MusicStandBlockEntity;
import aster.songweaver.system.spell.definition.Note;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.List;

public class MusicStandRenderer
        implements BlockEntityRenderer<MusicStandBlockEntity> {

    private final ItemRenderer itemRenderer;

    public MusicStandRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(
            MusicStandBlockEntity stand,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        ItemStack stack = stand.getStack();
        if (stack.isEmpty()) return;

        World world = stand.getWorld();
        if (world == null) return;

        BlockState state = stand.getCachedState();
        Direction facing = state.get(MusicStandBlock.FACING);

        matrices.push();

        /*
         * === 1. Move to block center ===
         */
        matrices.translate(0.5, 0.0, 0.5);

        /*
         * === 2. Rotate to match block facing ===
         * Blockstate rotations rotate the MODEL,
         * but NOT the BlockEntityRenderer.
         */
        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(
                        -facing.asRotation()
                )
        );

        /*
         * === 3. Move item onto the music plate ===
         *
         * Based on your model:
         * - Plate center is ~Z = +0.30
         * - Plate height is ~Y = 1.30
         */
        matrices.translate(
                0.0,    // X: centered
                1.30,   // Y: height above ground
                0.30    // Z: forward onto plate
        );

        /*
         * === 4. Tilt item to match stand angle ===
         * Your plate is rotated ~22.5° on Z in Blockbench
         */
        matrices.multiply(
                RotationAxis.POSITIVE_X.rotationDegrees(-22.5f)
        );

        /*
         * === 5. Scale item ===
         * FIXED transform assumes ~1 block = 1 unit
         */
        matrices.scale(0.75f, 0.75f, 0.75f);

        /*
         * === 6. Render ===
         */
        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                world,
                (int) stand.getPos().asLong()
        );

        renderNotes(stand, matrices, vertexConsumers, light);


        matrices.pop();
    }


    private void renderNotes(
            MusicStandBlockEntity stand,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light
    ) {
        if (!stand.hasNotes()) return;

        List<Note> notes = stand.getNotes();
        if (notes.isEmpty()) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float time = (stand.getWorld().getTime() % 360) + MinecraftClient.getInstance().getTickDelta();

        matrices.push();

        // Above the plate
        matrices.translate(0.0, 1.55, 0.0);

        // Gentle bob
        matrices.translate(0.0, MathHelper.sin(time * 0.05f) * 0.05f, 0.0);

        // Face the player
        matrices.multiply(
                MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation()
        );

        // Small & ethereal
        matrices.scale(-0.02f, -0.02f, 0.02f);

        float xOffset = -textRenderer.getWidth(notes.get(0).name()) / 2f;

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);

            float x = (i - (notes.size() - 1) / 2f) * 12f;

            textRenderer.draw(
                    note.name(),
                    x,
                    0,
                    0x80D8FF, // light blue
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertices,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    0,
                    light
            );
        }

        matrices.pop();
    }

}
