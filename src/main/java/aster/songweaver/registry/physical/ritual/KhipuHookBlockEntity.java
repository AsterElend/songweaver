package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.ImplementedInventory;
import aster.songweaver.registry.physical.KhipuItem;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KhipuHookBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> items =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    public KhipuHookBlockEntity(BlockPos pos, BlockState state) {
        super(LoomMiscRegistry.KHIPU_HOOK_ENTITY, pos, state);
    }

    /* ======================
       INVENTORY LOGIC
       ====================== */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack getStack(){ return items.get(0);}

    public void setItem(ItemStack newStack) {
        this.items.set(0, newStack);
        markDirty();

    }

    public ItemStack removeItem() {
        ItemStack old = items.get(0);
        items.set(0, ItemStack.EMPTY);
        markDirty();
        return old;
    }

    /* ======================
       RITUAL API
       ====================== */
@Nullable
    public BlockPos getStoredPos() {
        if (items.isEmpty() || !(items.get(0).getItem() instanceof KhipuItem)) return null;

    return KhipuItem.getBlockPos(items.get(0));
    }



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

    public boolean hasItem() {
    return items.isEmpty();
    }


}
