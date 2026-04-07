package aster.songweaver.api;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NadirToast implements Toast {
    private boolean justUpdated = true;
    private static final Identifier TEXTURE = new Identifier("songweaver", "textures/gui/forget_toast.png");
    private long startTime;
    private final Text advancement;
    private final Text title;

    public NadirToast(Text advancement, Text title){
        this.advancement = advancement;
        this.title = title;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        if (justUpdated) {
            this.startTime = startTime;
            justUpdated = false;
        }

        // Draw background (use vanilla toast texture size: 160x32)
        context.drawTexture(TEXTURE, 0, 0, 0, 0, 160, 32);

        // Draw text
        context.drawText(manager.getClient().textRenderer, title, 30, 7, 0x000000, true);
        context.drawText(manager.getClient().textRenderer, advancement, 30, 18, 0x666666, false);

        // Show for 5 seconds
        return startTime - this.startTime >= 5000
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    public static void sendPacket(Identifier advancement, boolean isForget){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeText(Text.translatable(advancement.toTranslationKey()));
        buf.writeBoolean(isForget);
    }

    public Text getTitle(){
        return title;
    }


    public static NadirToast buildRememberToast(Text advancement){
        return new NadirToast(advancement, Text.translatable("songweaver.toast.remember_title"));
    }
    public static NadirToast buildForgetToast(Text advancement){
        return new NadirToast(advancement, Text.translatable("songweaver.toast.forget_title"));
    }



}
