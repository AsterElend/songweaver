package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.NoteHolderItem;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.spell.definition.Note;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;

import java.util.List;

public class MusicStandBlockEntity extends BlockEntity {

    private ItemStack stack = ItemStack.EMPTY;

    public MusicStandBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.MUSIC_STAND_ENTITY, pos, state);
    }

    /* ---------------- Inventory ---------------- */

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        markDirty();
    }

    public ItemStack removeStack() {
        ItemStack result = stack;
        stack = ItemStack.EMPTY;
        markDirty();
        return result;
    }

    /* ---------------- Ritual API ---------------- */

    /** Called by your ritual system */
    public List<Note> getNotes() {
        if (stack.isEmpty()) return List.of();
        return NoteHolderItem.NotestoreUtil.readNotes(stack);
    }

    public boolean hasNotes() {
        return !stack.isEmpty() && !NoteHolderItem.NotestoreUtil.notHasNotes(stack);
    }

    /* ---------------- Persistence ---------------- */

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!stack.isEmpty()) {
            nbt.put("Item", stack.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Item")) {
            stack = ItemStack.fromNbt(nbt.getCompound("Item"));
        } else {
            stack = ItemStack.EMPTY;
        }
    }
}
