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
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;


public class MusicStandRenderer implements BlockEntityRenderer<MusicStandBlockEntity> {

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
        ItemStack stack = stand.getStack(0);
        if (stack.isEmpty()) return;

        World world = stand.getWorld();
        if (world == null) return;

        BlockState state = stand.getCachedState();
        Direction facing = state.get(MusicStandBlock.FACING);

        matrices.push();

        // === 1. Move to block center ===
        matrices.translate(0.5, 0.0, 0.5);

        // === 2. Rotate to match block facing ===
        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation())
        );

        // === 3. Move item onto the music plate (center of shelf) ===
        // Based on your Blockbench model: shelf is roughly Y = 1.30, Z = 0.35
        matrices.translate(0.0, 1.30, 0.35);

        // === 4. Tilt item to match plate angle (~22.5°) ===
        matrices.multiply(
                RotationAxis.POSITIVE_X.rotationDegrees(-22.5f)
        );

        // === 5. Scale item down to fit ===
        matrices.scale(0.75f, 0.75f, 0.75f);

        // === 6. Render the item ===
        int seed = (int) stand.getPos().asLong(); // optional: or use stand.getWorld().getRandom().nextInt()
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


        // === 7. Render notes above the shelf ===
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

        // === Move to center of shelf and slightly above ===
        matrices.translate(0.5, 1.55, 0.5); // center X/Z, Y slightly above shelf

        // Gentle vertical bob
        matrices.translate(0.0, MathHelper.sin(time * 0.05f) * 0.05f, 0.0);

        // Rotate to face the player
        matrices.multiply(
                MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation()
        );

        // Scale down for ethereal floating text
        matrices.scale(1f, 1f, 1f);


        // Space notes evenly
        float spacing = 12f;
        float offset = (notes.size() - 1) * spacing / 2f;

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            float x = i * spacing - offset;

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

    @Override
    public boolean rendersOutsideBoundingBox(MusicStandBlockEntity entity) {
        return true;
    }
}
