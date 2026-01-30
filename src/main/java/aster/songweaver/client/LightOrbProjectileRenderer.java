package aster.songweaver.client;


import aster.songweaver.registry.physical.LightOrbProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;


@Environment(EnvType.CLIENT)
public class LightOrbProjectileRenderer extends EntityRenderer<LightOrbProjectileEntity> {

    private final ItemRenderer itemRenderer;

    public LightOrbProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(LightOrbProjectileEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();

        // Center and scale the projectile
        matrices.translate(0.0, 0.0, 0.0);
        matrices.scale(0.5f, 0.5f, 0.5f); // make it smaller than a full block

        // Optionally rotate for a spinning effect
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.age * 20f));

        // Render a yellow concrete block as an item
        itemRenderer.renderItem(new ItemStack(Blocks.YELLOW_CONCRETE), ModelTransformationMode.GROUND,
                light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();

        // Spawn End Rod particles
        if (entity.getWorld().isClient && entity.getWorld().random.nextFloat() < 0.2f) {
            entity.getWorld().addParticle(ParticleTypes.END_ROD,
                    entity.getX(), entity.getY(), entity.getZ(),
                    (entity.getWorld().random.nextDouble() - 0.5) * 0.05,
                    (entity.getWorld().random.nextDouble() - 0.5) * 0.05,
                    (entity.getWorld().random.nextDouble() - 0.5) * 0.05);
        }
    }

    @Override
    public Identifier getTexture(LightOrbProjectileEntity entity) {
        return null; // Not needed when using ItemRenderer
    }
}
