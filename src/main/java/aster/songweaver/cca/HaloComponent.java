package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
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
import java.util.List;

public class HaloComponent implements AutoSyncedComponent {

    private final PlayerEntity player;

    public HaloComponent(PlayerEntity player){
        this.player = player;
    }

    private final DefaultedList<ItemStack> stacks =
            DefaultedList.ofSize(16, ItemStack.EMPTY);

    public DefaultedList<ItemStack> getStacks(){
        return stacks;
    }
    public void push(ItemStack stack) {
        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).isEmpty()) {
                stacks.set(i, stack.copyWithCount(1));
                SongweaverComponents.HALO.sync(player);
                return;
            }
        }
    }


    public ItemStack pop(@Nullable Item preferred) {
        if (preferred != null) {
            for (int i = stacks.size() - 1; i >= 0; i--) {
                ItemStack s = stacks.get(i);
                if (!s.isEmpty() && s.isOf(preferred)) {
                    stacks.set(i, ItemStack.EMPTY);
                    SongweaverComponents.HALO.sync(player);
                    return s;

                }
            }

        }

        // fallback: last added
        for (int i = stacks.size() - 1; i >= 0; i--) {
            ItemStack s = stacks.get(i);
            if (!s.isEmpty()) {
                stacks.set(i, ItemStack.EMPTY);
                SongweaverComponents.HALO.sync(player);
                return s;
            }
        }
        SongweaverComponents.HALO.sync(player);
        return ItemStack.EMPTY;
    }

    public void purge() {
        Collections.fill(stacks, ItemStack.EMPTY);

        SongweaverComponents.HALO.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        Collections.fill(stacks, ItemStack.EMPTY);

        NbtList list = nbt.getList("Halo", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 255;

            if (slot < stacks.size()) {
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

