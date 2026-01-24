package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.physical.KhipuItem;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KhipuHookBlockEntity extends BlockEntity {
    private ItemStack stack = ItemStack.EMPTY;

    public KhipuHookBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.KHIPU_HOOK_ENTITY, pos, state);
    }

    /* ======================
       INVENTORY LOGIC
       ====================== */

    public boolean hasItem() {
        return !stack.isEmpty();
    }

    public void setItem(ItemStack newStack) {
        this.stack = newStack;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public ItemStack getItem() {
        return stack;
    }

    public ItemStack removeItem() {
        ItemStack old = stack;
        stack = ItemStack.EMPTY;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        return old;
    }

    /* ======================
       RITUAL API
       ====================== */
@Nullable
    public BlockPos getStoredPos() {
        if (stack.isEmpty() || !(stack.getItem() instanceof KhipuItem)) return null;

    return KhipuItem.getBlockPos(stack);
    }


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
        if (nbt.contains("Item", NbtElement.COMPOUND_TYPE)) {
            stack = ItemStack.fromNbt(nbt.getCompound("Item"));
        }
    }

}
