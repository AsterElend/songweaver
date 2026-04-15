package aster.songweaver.api;

import aster.songweaver.Songweaver;
import aster.songweaver.api.renderNonsense.NadirToast;
import aster.songweaver.client.ClientWardedState;
import aster.songweaver.registry.LoomTags;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.api.cast.DraftExecutor;
import aster.songweaver.api.spell.loaders.RitualReloadListener;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Note;
import aster.songweaver.api.weaving.RitualDefinition;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static aster.songweaver.registry.physical.item.WardingPrismItem.syncBatch;

public class SongweaverPackets {

    public static final Identifier SYNC_WARDS = new Identifier("songweaver", "ward_sync");

    public static final Identifier CAST_DRAFT_PACKET =
            new Identifier("songweaver", "cast_draft");

    public static final Identifier CAST_FAILURE_PACKET =
            new Identifier("songweaver", "cast_failure");

    public static final Identifier FIRE_NADIR_TOAST = Songweaver.locate("fire_nadir_toast");



    public static void registerServer() {

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

            server.execute(() -> {
                server.execute(() -> { // 1 tick delay

                    ServerPlayerEntity player = handler.player;
                    ServerWorld world = player.getServerWorld();

                    WardedBlocksState state = WardedBlocksState.get(world);

                    syncBatch(world, state.getAllPositions(), true);
                });
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(
                CAST_DRAFT_PACKET,
                (server, player, handler, buf, responseSender) -> {

                    List<Note> notes = readNotes(buf);

                    if (player.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)) {
                        SongweaverPackets.sendFeedback(
                                player,
                                CastFeedback.SILENCED
                        );
                        return;
                    }

                    server.execute(() -> {

                        ItemStack offhand = player.getOffHandStack();

                        if (offhand.isIn(LoomTags.INTERCEPT_ITEMS) ) {

                            NoteHolderItem.NotestoreUtil.storeNotes(offhand, notes);

                            SongweaverPackets.sendFeedback(player, CastFeedback.NOTE_INTERCEPT);

                            return; //  DO NOT CAST
                        }

                        GrandLoomBlockEntity controller =
                                GrandLoomBlockEntity.findNearby(player);

                        if (controller != null) {

                            if (controller.hasActiveRitual()) {
                                SongweaverPackets.sendFeedback(
                                        player,
                                        CastFeedback.RITUAL_BUSY
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
                        DraftExecutor.cast(player, notes);
                    });
                }
        );

    }

    public static void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver(SYNC_WARDS, (client, handler, buf, responseSender) ->{
            if (buf.readableBytes() > 9) {

                int count = buf.readInt();
                boolean warded = buf.readBoolean();

                List<Long> positions = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    positions.add(buf.readLong());
                }

                client.execute(() -> {
                    for (long pos : positions) {
                        ClientWardedState.set(pos, warded);
                    }
                });

            } else {

                long pos = buf.readLong();
                boolean warded = buf.readBoolean();

                client.execute(() -> {
                    ClientWardedState.set(pos, warded);
                });
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FIRE_NADIR_TOAST, (client, handler, buf, responseSender) ->{
          Text advancement = buf.readText();
          boolean isForget = buf.readBoolean();

          if (isForget){
              client.execute(()->{
                  client.getToastManager().add(NadirToast.buildForgetToast(advancement));
              });
           } else {
              client.execute(()->{
                  client.getToastManager().add(NadirToast.buildRememberToast(advancement));
              });

           }
        });
    }

    private static List<Note> readNotes(PacketByteBuf buf) {
        int size = buf.readVarInt();
        List<Note> notes = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            notes.add(buf.readEnumConstant(Note.class));
        }

        return List.copyOf(notes);
    }


    public static void sendFeedback(ServerPlayerEntity player,
                                    CastFeedback reason) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(reason);

        ServerPlayNetworking.send(
                player,
                CAST_FAILURE_PACKET,
                buf
        );
    }
}
