package aster.songweaver.registry;

import aster.songweaver.system.spell.definition.Note;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NoteHolderItem extends Item {
    public NoteHolderItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {

        if (NotestoreUtil.notHasNotes(stack)) return;

        List<Note> notes = NotestoreUtil.readNotes(stack);
        if (notes.isEmpty()) return;

        tooltip.add(
                Text.literal("Notes:")
                        .formatted(Formatting.AQUA)
        );

        for (Note note : notes) {
            tooltip.add(
                    Text.literal(" • " + note.name())
                            .formatted(Formatting.GRAY)
            );
        }
    }


    public final class NotestoreUtil {

        private static final String NOTES_KEY = "Notes";

        public static boolean notHasNotes(ItemStack stack) {
            return !stack.hasNbt()
                    || !stack.getNbt().contains(NOTES_KEY);
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
            if (notHasNotes(stack)) return List.of();

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
