package aster.songweaver.system.cast;

import aster.songweaver.system.definition.Note;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class CastPlayback {
    private static final SoundEvent HARP =
            SoundEvents.BLOCK_NOTE_BLOCK_HARP.value();

    private CastPlayback() {}

    public static void play(ServerWorld world,
                            Vec3d pos,
                            List<Note> notes) {

        long startTick = world.getTime();

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            int delay = i * 2; // 2 ticks between notes

            world.getServer().execute(() -> {
                if (world.getTime() >= startTick + delay) {
                    world.playSound(
                            null,
                            pos.x, pos.y, pos.z,
                            HARP,
                            SoundCategory.PLAYERS,
                            1.0f,
                            note.pitch()
                    );
                }
            });
        }
    }
}
