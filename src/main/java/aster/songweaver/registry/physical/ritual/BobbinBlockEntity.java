package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.ImplementedInventory;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BobbinBlockEntity extends BlockEntity implements ImplementedInventory {


    private final DefaultedList<ItemStack> items =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BobbinBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.BOBBIN_ENTITY, pos, state);
    }

    /* ───────────────────────── Inventory ───────────────────────── */

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack getStack() {
        return items.get(0);
    }

    public int removeItems(int amount) {
        ItemStack removed = Inventories.splitStack(items, 0, amount);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed.getCount();
    }

    public void clearStack(int slot){
        items.set(slot, ItemStack.EMPTY);
        markDirty();
    }

    public void setStack(int slot, ItemStack stack){
        this.items.set(slot, stack);
        markDirty();
    }





    /* ───────────────────────── Sync ───────────────────────── */

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


    /* ───────────────────────── NBT ───────────────────────── */

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

    /* ───────────────────────── Networking ───────────────────────── */

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
