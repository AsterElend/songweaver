package aster.songweaver.api.renderNonsense;

import aster.songweaver.Songweaver;
import aster.songweaver.api.cast.SongweaverPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class NadirToast implements Toast {
    private boolean justUpdated = true;
    private static final Identifier FORGET_TEXTURE = new Identifier("songweaver", "textures/gui/forget_toast.png");
    private static final Identifier REMEMBER_TEXTURE = new Identifier("songweaver", "textures/gui/remember_toast.png");
    private long startTime;
    private final Text advancement;
    private final Text title;
    private final Identifier texture;

    public NadirToast(Text advancement, Text title, Identifier texture){
        this.advancement = advancement;
        this.title = title;
        this.texture = texture;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        if (justUpdated) {
            this.startTime = startTime;
            justUpdated = false;
        }

        // Draw background (use vanilla toast texture size: 160x32)
        context.drawTexture(this.texture, 0, 0, 0, 0, 160, 32, 160, 32);

        // Draw text
        context.drawText(manager.getClient().textRenderer, title, 30, 7, 0x000000, false);
        context.drawText(manager.getClient().textRenderer, advancement, 30, 18, 0x666666, false);

        // Show for 5 seconds
        return startTime - this.startTime >= 5000
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    public static void sendPacket(Identifier advancement, boolean isForget, PlayerEntity player){
        PacketByteBuf buf = PacketByteBufs.create();
        Text output;
        if (Objects.equals(advancement, Songweaver.locate("everything"))){
            output = Text.translatable("songweaver.toast.everything");
        } else {
             output = player.getServer().getAdvancementLoader().get(advancement).getDisplay().getTitle();
        }



        buf.writeText(output);
        buf.writeBoolean(isForget);
        ServerPlayNetworking.send((ServerPlayerEntity) player, SongweaverPackets.FIRE_NADIR_TOAST, buf);
    }



    public static NadirToast buildRememberToast(Text advancement){
        return new NadirToast(advancement, Text.translatable("songweaver.toast.remember_title"), REMEMBER_TEXTURE);
    }
    public static NadirToast buildForgetToast(Text advancement){
        return new NadirToast(advancement, Text.translatable("songweaver.toast.forget_title"), FORGET_TEXTURE);
    }



}
