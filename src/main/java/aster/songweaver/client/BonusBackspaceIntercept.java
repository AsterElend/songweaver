package aster.songweaver.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

public class BonusBackspaceIntercept {
    private static boolean wasDown = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (DistaffHelper.isNotHoldingDistaff()) {
                wasDown = false;
                return;
            }

            long window = client.getWindow().getHandle();
            boolean down = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_BACKSPACE) == GLFW.GLFW_PRESS;

            if (down && !wasDown) {
                InputBuffer.pop();
            }

            wasDown = down;
        });
    }
}
