package aster.songweaver.system.cast;

import aster.songweaver.registry.LoomMiscRegistry;
import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.RitualReloadListener;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Note;
import aster.songweaver.system.spell.definition.RitualDefinition;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
public class SongServerCasting {

    public static final Identifier CAST_DRAFT_PACKET =
            new Identifier("songweaver", "cast_draft");

    public static final Identifier CAST_FAILURE_PACKET =
            new Identifier("songweaver", "cast_failure");

    public static void registerServer() {

        ServerPlayNetworking.registerGlobalReceiver(
                CAST_DRAFT_PACKET,
                (server, player, handler, buf, responseSender) -> {

                    List<Note> notes = readNotes(buf);

                    if (player.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)) {
                        SongServerCasting.sendFailure(
                                player,
                                CastFailure.SILENCED
                        );
                        return;
                    }

                    server.execute(() -> {

                        RitualControllerBlockEntity controller =
                                RitualControllerBlockEntity.findNearby(player);

                        if (controller != null) {

                            if (controller.hasActiveRitual()) {
                                SongServerCasting.sendFailure(
                                        player,
                                        CastFailure.RITUAL_BUSY
                                );
                                return;
                            }

                            RitualDefinition ritual =
                                    RitualReloadListener.matchForRitual(notes);

                            if (ritual != null) {
                                controller.tryStartRitual(player, ritual);
                                return; // correctly exits on server thread
                            }
                        }

                        // Fall through to normal casting
                        CastExecutor.cast(player, notes);
                    });
                }
        );

    }

    private static List<Note> readNotes(PacketByteBuf buf) {
        int size = buf.readVarInt();
        List<Note> notes = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            notes.add(buf.readEnumConstant(Note.class));
        }

        return List.copyOf(notes);
    }

    // keep this public – executor will use it
    public static void sendFailure(ServerPlayerEntity player,
                                   CastFailure reason) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(reason);

        ServerPlayNetworking.send(
                player,
                CAST_FAILURE_PACKET,
                buf
        );
    }
}
