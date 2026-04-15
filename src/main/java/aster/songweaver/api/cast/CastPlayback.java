package aster.songweaver.api.cast;

import aster.songweaver.api.weaving.Note;
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
    public static void play(ServerWorld world, Vec3d pos, List<Note> notes){
        long startTick = world.getTime();
        for (int i = 0; i< notes.size(); i++){
            Note note = notes.get(i);
            int delay = i * 8;
            schedule(world, startTick + delay, () -> {
                if (note == Note.REST) return;


                world.playSound(
                        null,
                        pos.x, pos.y, pos.z,
                        HARP,
                        SoundCategory.PLAYERS,
                        3.0f,
                        note.pitch()
                );
        });
        }
    }
    public static void playFailure(ServerWorld world,
                                   Vec3d pos,
                                   List<Note> notes) {

        long startTick = world.getTime();

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            int delay = i * 8;

            schedule(world, startTick + delay, () -> {
                if (note == Note.REST) return;

                // 🎵 Main note
                world.playSound(
                        null,
                        pos.x, pos.y, pos.z,
                        HARP,
                        SoundCategory.PLAYERS,
                        3.0f,
                        note.pitch()
                );

                // 🌊 Reverb / echo layers
                int echoes = 3; // tweak this

                for (int e = 1; e <= echoes; e++) {
                    int echoDelay = e * 3;

                    int finalE = e;
                    schedule(world, world.getTime() + echoDelay, () -> {
                        world.playSound(
                                null,
                                pos.x, pos.y, pos.z,
                                HARP,
                                SoundCategory.PLAYERS,
                                3.0f * (0.6f / finalE),        // quieter each echo
                                note.pitch() * (1.0f - finalE * 0.03f) // slight pitch drop
                        );
                    });
                }
            });
        }
    }

    private static void schedule(ServerWorld world, long targetTick, Runnable task) {
        world.getServer().execute(() -> {
            if (world.getTime() >= targetTick) {
                task.run();
            } else {
                schedule(world, targetTick, task); // retry next tick
            }
        });
    }
}
