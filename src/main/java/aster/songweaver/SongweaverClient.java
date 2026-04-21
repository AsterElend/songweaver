package aster.songweaver;

import aster.songweaver.api.cast.SongweaverPackets;
import aster.songweaver.api.renderNonsense.SiphonRenderState;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.client.*;
import aster.songweaver.registry.SongweaverParticles;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomFluids;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SongweaverClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BufferReset.init();
        SongHud.init();
        BonusBackspaceIntercept.init();

        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.BOBBIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.GRAND_LOOM, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.FRACTAL_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.FRACTAL_SAPLING, RenderLayer.getCutout());

        ParticleFactoryRegistry.getInstance().register(SongweaverParticles.SILK_PARTICLE, SongweaverParticles.SilkParticleFactory::new);

        BlockEntityRendererFactories.register(LoomBlockEntities.BOBBIN_ENTITY, BobbinBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(LoomBlockEntities.MUSIC_STAND_ENTITY, MusicStandRenderer::new);
        BlockEntityRendererFactories.register(LoomBlockEntities.KHIPU_HOOK_ENTITY, KhipuHookBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(LoomBlockEntities.RIFT_BLOCK_ENTITY, RiftBERenderer::new);

        EntityRendererRegistry.register(LoomMiscRegistry.LIGHT_ORB_PROJECTILE, LightOrbProjectileRenderer::new);

        WorldRenderEvents.AFTER_TRANSLUCENT.register(WardedBlockRenderer::render);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(SiphonRenderer::render);
        SongweaverPackets.registerClient();

        ClientTickEvents.END_WORLD_TICK.register(SiphonRenderState::tickCleanup);




        ClientPlayNetworking.registerGlobalReceiver(
                SongweaverPackets.CAST_FAILURE_PACKET,
                (client, handler, buf, responseSender) -> {

                    CastFeedback reason = buf.readEnumConstant(CastFeedback.class);

                    client.execute(() -> {
                        if (client.player != null) {
                            client.player.sendMessage(
                                    Text.literal(reason.message(false)).formatted(Formatting.RED),
                                    true
                            );
                        }
                    });
                }
        );

        WorldRenderEvents.AFTER_TRANSLUCENT.register(WardedBlockRenderer::render);


        CoreShaderRegistrationCallback.EVENT.register(context -> context.register(
                new Identifier("songweaver", "tear_portal"),
                VertexFormats.POSITION_COLOR_TEXTURE,
                shader -> RiftBERenderer.SHADER = shader
        ));


        FluidRenderHandlerRegistry.INSTANCE.register(LoomFluids.LETHEAN_WATER_STATIC, LoomFluids.LETHEAN_WATER_FLOWING, SimpleFluidRenderHandler.coloredWater(0xff209f));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), LoomFluids.LETHEAN_WATER_STATIC, LoomFluids.LETHEAN_WATER_FLOWING);




    }
}
