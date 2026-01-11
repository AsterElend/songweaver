package aster.songweaver;

import aster.songweaver.client.BonusBackspaceIntercept;
import aster.songweaver.client.BufferReset;
import aster.songweaver.client.SongHud;
import aster.songweaver.system.definition.CastFailure;
import aster.songweaver.system.cast.SongServerCasting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SongweaverClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BufferReset.init();
        SongHud.init();
        BonusBackspaceIntercept.init();
        ClientPlayNetworking.registerGlobalReceiver(
                SongServerCasting.CAST_FAILURE_PACKET,
                (client, handler, buf, responseSender) -> {

                    CastFailure reason = buf.readEnumConstant(CastFailure.class);

                    client.execute(() -> {
                        if (client.player != null) {
                            client.player.sendMessage(
                                    Text.literal(reason.message()).formatted(Formatting.RED),
                                    true
                            );
                        }
                    });
                }
        );

    }
}
