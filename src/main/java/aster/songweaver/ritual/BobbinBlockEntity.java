package aster.songweaver.ritual;

import aster.songweaver.registry.LoomMiscRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class BobbinBlockEntity extends BlockEntity {

    private ItemStack stack = ItemStack.EMPTY;

    public BobbinBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.BOBBIN_ENTITY, pos, state);
    }

    // ─── Storage access ─────────────────────────────

    public ItemStack getStack() {
        return stack;
    }


    public int removeItems(int amount){
        if (stack.isEmpty()) return 0;

        int removed = Math.min(amount, stack.getCount());
        stack.decrement(removed);

        if (stack.isEmpty()) {
            stack = ItemStack.EMPTY;
        }

        markDirty();
        return removed;
    }

    // ─── Player interaction ─────────────────────────

    /** Attempts to insert ONE item from the player into this bobbin */
    public boolean insertFromPlayer(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);
        if (held.isEmpty()) return false;

        // Bobbin empty → take one
        if (stack.isEmpty()) {
            stack = held.split(1);
            markDirty();
            return true;
        }

        // Same item → add one
        if (ItemStack.canCombine(stack, held)) {
            stack.increment(1);
            held.decrement(1);
            markDirty();
            return true;
        }

        return false;
    }

    /** Removes items from THIS bobbin into the player inventory */
    public boolean extractToPlayer(PlayerEntity player, boolean fullStack) {
        if (stack.isEmpty()) return false;

        int amount = fullStack ? stack.getCount() : 1;
        ItemStack extracted = stack.split(amount);

        if (!player.getInventory().insertStack(extracted)) {
            player.dropItem(extracted, false);
        }

        if (stack.isEmpty()) {
            stack = ItemStack.EMPTY;
        }

        markDirty();
        return true;
    }

    // ─── NBT ─────────────────────────────────────────

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!stack.isEmpty()) {
            nbt.put("Stack", stack.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        stack = nbt.contains("Stack")
                ? ItemStack.fromNbt(nbt.getCompound("Stack"))
                : ItemStack.EMPTY;
    }
}
