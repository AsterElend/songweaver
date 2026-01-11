package aster.songweaver.items;

import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.definition.Note;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SpindleItem extends Item {
    public static final int COOLDOWN = 10;
    public SpindleItem(Settings settings) {
        super(settings);
    }
    public static final int COOLDOWN_TICKS = 40;



    @Override
    public TypedActionResult<ItemStack> use(World world,
                                            PlayerEntity user,
                                            Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            return TypedActionResult.pass(stack);
        }

        if (hand != Hand.MAIN_HAND) {
            return TypedActionResult.pass(stack);
        }

        if (!SpindleUtil.hasNotes(stack)) {
            return TypedActionResult.pass(stack);
        }

        List<Note> notes = SpindleUtil.readNotes(stack);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(notes.size());
        for (Note note : notes) {
            buf.writeEnumConstant(note);
        }

        ClientPlayNetworking.send(
                SongServerCasting.CAST_DRAFT_PACKET,
                buf
        );


        return TypedActionResult.consume(stack);
    }

    public static final class SpindleUtil {

        private static final String NOTES_KEY = "Notes";

        public static boolean hasNotes(ItemStack stack) {
            return stack.hasNbt()
                    && stack.getNbt().contains(NOTES_KEY);
        }

        public static void storeNotes(ItemStack stack,
                                      List<Note> notes) {

            NbtList list = new NbtList();
            for (Note note : notes) {
                list.add(NbtString.of(note.name()));
            }

            stack.getOrCreateNbt().put(NOTES_KEY, list);
        }

        public static List<Note> readNotes(ItemStack stack) {
            if (!hasNotes(stack)) return List.of();

            NbtList list = stack.getNbt().getList(
                    NOTES_KEY,
                    NbtElement.STRING_TYPE
            );

            List<Note> notes = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                notes.add(Note.valueOf(list.getString(i)));
            }

            return List.copyOf(notes);
        }
    }

}
