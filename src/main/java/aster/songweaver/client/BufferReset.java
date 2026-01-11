package aster.songweaver.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class BufferReset {

    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (minecraftClient.player == null) return;

            if (!DistaffHelper.isHoldingDistaff()){
                InputBuffer.clear();
            }
        });
    }
}
