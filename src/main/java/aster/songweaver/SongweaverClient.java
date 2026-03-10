package aster.songweaver;

import aster.songweaver.client.*;
import aster.songweaver.registry.physical.LightOrbProjectileEntity;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.cast.SongServerCasting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class SongweaverClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BufferReset.init();
        SongHud.init();
        BonusBackspaceIntercept.init();

        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.BOBBIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.RITUAL_CONTROLLER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.FRACTAL_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomBlockStuff.FRACTAL_SAPLING, RenderLayer.getCutout());



        BlockEntityRendererFactories.register(LoomBlockStuff.BOBBIN_ENTITY, BobbinBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(LoomBlockStuff.MUSIC_STAND_ENTITY, MusicStandRenderer::new);
        BlockEntityRendererFactories.register(LoomBlockStuff.KHIPU_HOOK_ENTITY, KhipuHookBlockEntityRenderer::new);

        EntityRendererRegistry.register(LoomMiscRegistry.LIGHT_ORB_PROJECTILE, LightOrbProjectileRenderer::new);






        ClientPlayNetworking.registerGlobalReceiver(
                SongServerCasting.CAST_FAILURE_PACKET,
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

    }
}
