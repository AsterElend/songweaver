package aster.songweaver.client;

import aster.songweaver.registry.LoomTags;

import aster.songweaver.system.definition.Note;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;


public class DistaffHelper {

    public static boolean isHoldingDistaff() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        ItemStack stack = client.player.getMainHandStack();
        return stack.isIn(LoomTags.DISTAFFS);
    }

    public static void playNote(MinecraftClient client, Note note) {
        if (client.player == null) return;
        client.player.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
                1.0F,
                note.pitch()
        );
    }

}
