package aster.songweaver;

import aster.songweaver.client.*;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.cast.SongServerCasting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
@SuppressWarnings("deprecation")
public class SongweaverClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BufferReset.init();
        SongHud.init();
        BonusBackspaceIntercept.init();

        BlockRenderLayerMap.INSTANCE.putBlock(LoomMiscRegistry.BOBBIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoomMiscRegistry.RITUAL_CONTROLLER, RenderLayer.getCutout());


        BlockEntityRendererFactories.register(LoomMiscRegistry.BOBBIN_ENTITY, BobbinBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(LoomMiscRegistry.MUSIC_STAND_ENTITY, MusicStandRenderer::new);
        BlockEntityRendererFactories.register(LoomMiscRegistry.KHIPU_HOOK_ENTITY, KhipuHookBlockEntityRenderer::new);





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
