package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class HaloComponent implements AutoSyncedComponent {

    private final PlayerEntity player;

    // Always initialize to default 16 empty slots
    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(16, ItemStack.EMPTY);

    public HaloComponent(PlayerEntity player) {
        this.player = player;
    }

    public DefaultedList<ItemStack> getStacks() {
        return stacks;
    }

    public void push(ItemStack stack) {
        if (stack.isEmpty()) return;

        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).isEmpty()) {
                stacks.set(i, stack.copyWithCount(1));
                SongweaverComponents.HALO.sync(player);
                return;
            }
        }
    }

    public void pop(@Nullable Item preferred) {
        if (preferred != null) {
            for (int i = stacks.size() - 1; i >= 0; i--) {
                ItemStack s = stacks.get(i);
                if (!s.isEmpty() && s.isOf(preferred)) {
                    stacks.set(i, ItemStack.EMPTY);
                    SongweaverComponents.HALO.sync(player);
                    return;
                }
            }
        }

        // fallback: last added
        for (int i = stacks.size() - 1; i >= 0; i--) {
            ItemStack s = stacks.get(i);
            if (!s.isEmpty()) {
                stacks.set(i, ItemStack.EMPTY);
                SongweaverComponents.HALO.sync(player);
                return;
            }
        }

        SongweaverComponents.HALO.sync(player);
    }

    public void purge() {
        Collections.fill(stacks, ItemStack.EMPTY);
        SongweaverComponents.HALO.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        // Always start fresh
        Collections.fill(stacks, ItemStack.EMPTY);

        // Check if the tag exists (old worlds might not have it)
        if (!nbt.contains("Halo", NbtElement.LIST_TYPE)) {
            // Old world, nothing to read → default stacks remain
            return;
        }

        NbtList list = nbt.getList("Halo", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 255;

            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.fromNbt(entry));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                NbtCompound entry = new NbtCompound();
                entry.putByte("Slot", (byte) i);
                stack.writeNbt(entry);
                list.add(entry);
            }
        }

        nbt.put("Halo", list);
    }
}

