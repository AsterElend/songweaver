package aster.songweaver.client;

import aster.songweaver.cca.SongweaverComponents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HaloFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final MinecraftClient client;

    public HaloFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, MinecraftClient client) {
        super(context);
        this.client = client;
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            float limbSwing,
            float limbSwingAmount,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        var component = SongweaverComponents.HALO.get(player);
        if (component == null) return;

        List<ItemStack> stacks = component.getStacks();
        if (stacks.isEmpty()) return;

        // Push once for the entire feature
        matrices.push();

        // Translate to the player's head
        ModelPart head = this.getContextModel().head;
        matrices.translate(
                head.pivotX / 16.0f,        // usually 0
                head.pivotY / 16.0f + 0.25, // 0.25 is a small offset to move above head center
                head.pivotZ / 16.0f         // usually 0
        );


        float time = (player.age + tickDelta) * 0.05f;
        int n = stacks.size();

        for (int i = 0; i < n; i++) {
            float angle = time + ((float) i / n) * 2f * (float) Math.PI;
            float radius = 0.3f;
            float x = (float)Math.cos(angle) * radius;
            float z = (float)Math.sin(angle) * radius;
            float y = -1f;

            // Push per item, pop after rendering it
            matrices.push();
            matrices.translate(x, y, z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 20));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headPitch));
            matrices.scale(0.5f, 0.5f, 0.5f);

            client.getItemRenderer().renderItem(stacks.get(i), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, player.getWorld(), i);

            matrices.pop(); // must pop here
        }

        matrices.pop(); // pop the initial push
    }

}
