package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.ImplementedInventory;
import aster.songweaver.registry.NoteHolderItem;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.spell.definition.Note;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MusicStandBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> items =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    public MusicStandBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.MUSIC_STAND_ENTITY, pos, state);
    }

    /* ---------------- Inventory ---------------- */

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void setStack(int slot, ItemStack stack){
        this.items.set(slot, stack);
        markDirty();
    }
    /* ---------------- Ritual API ---------------- */

    /** Called by your ritual system */
    public List<Note> getNotes() {
        if (items.isEmpty()) return List.of();
        return NoteHolderItem.NotestoreUtil.readNotes(items.get(0));
    }

    public boolean hasNotes() {
        return !items.isEmpty() && !NoteHolderItem.NotestoreUtil.notHasNotes(items.get(0));
    }

    /* ---------------- Persistence ---------------- */

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(
                    pos,
                    getCachedState(),
                    getCachedState(),
                    Block.NOTIFY_ALL
            );

            ServerWorld worldSer = (ServerWorld) world;

            worldSer.getChunkManager().markForUpdate(pos);
        }
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
