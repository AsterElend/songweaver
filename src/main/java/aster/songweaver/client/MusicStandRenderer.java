package aster.songweaver.client;

import aster.songweaver.api.weaving.Note;
import aster.songweaver.registry.physical.be.MusicStandBlockEntity;
import aster.songweaver.registry.physical.block.MusicStandBlock;
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
        matrices.translate(0.5, -0.3, 0.6);

        // === 2. Rotate to match block facing ===
        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation())
        );

        // === 3. Move item onto the music plate (center of shelf) ===
        // Based on your Blockbench model: shelf is roughly Y = 1.30, Z = 0.35
        matrices.translate(0.0, 1, 0.35);


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

        matrices.pop();

        renderNotes(stand, facing, matrices, vertexConsumers, light);
    }

    private void renderNotes(
            MusicStandBlockEntity stand,
            Direction facing,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light
    ) {
        if (!stand.hasNotes()) return;
        List<Note> notes = stand.getNotes();
        if (notes.isEmpty()) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float time = stand.getWorld().getTime() + MinecraftClient.getInstance().getTickDelta();

        matrices.push();

        // 1. Block center, at a fixed hover height above the stand
        matrices.translate(0.5, 1.6, 0.5);

        // 2. Gentle bob
        matrices.translate(0.0, MathHelper.sin(time * 0.05f) * 0.05f, 0.0);

        // 3. Billboard: face the camera
        matrices.multiply(
                MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation()
        );

        // 4. CRITICAL: scale way down — text is ~8 units tall by default
        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale); // negative X/Y flips it right-side up

        float spacing = 12f;
        float offset = (notes.size() - 1) * spacing / 2f;

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            float x = i * spacing - offset;

            textRenderer.draw(
                    note.name(),
                    x,
                    0,
                    0x80D8FF,
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
