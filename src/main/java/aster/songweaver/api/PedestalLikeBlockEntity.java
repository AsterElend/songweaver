package aster.songweaver.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
public abstract class PedestalLikeBlockEntity extends BlockEntity implements ImplementedInventory{


    public DefaultedList<ItemStack> items;
    public final int SIZE = 1;

    public PedestalLikeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);
        this.items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    }

    public void clearStack(){
        items.set(0, ItemStack.EMPTY);
        inventoryChanged();
    }

    public ItemStack getStack(){
        return items.get(0);
    }

    public void setStack(ItemStack stack){
        items.set(0, stack);
        inventoryChanged();
    }
    public void setCount(int count){
        items.get(0).setCount(count);
        inventoryChanged();
    }
    public int getCount(){
        return items.get(0).getCount();
    }
    public int getMaxCount(){
        return items.get(0).getMaxCount();
    }


    public ItemStack stackInteractionAttempt(ItemStack inputStack){
        ItemStack stack = inputStack;

        if (getStack().isEmpty() && stack.isEmpty()){
            return stack;
        }

        if (stack.isEmpty()){
            stack = getStack();

                    clearStack();
            inventoryChanged();

            return stack;
        }

        if (getStack().isEmpty()){
            setStack(stack);
            stack = ItemStack.EMPTY;
            inventoryChanged();
            return stack;
        }

        if (getStack().getItem() == stack.getItem()){
            if (getStack().getCount() + stack.getCount() < getStack().getMaxCount()) {
                int newCount = getStack().getCount() + stack.getCount();
                setCount(newCount);
                stack = ItemStack.EMPTY;
                inventoryChanged();
                return stack;
            } else {
                int movingCount = getStack().getMaxCount() - getStack().getCount();
                stack.setCount(stack.getCount() - movingCount);
                setCount(getStack().getMaxCount());
                inventoryChanged();
                return stack;
            }
        }

        stack = swapStacks(stack);

        return stack;
    }

    public ItemStack swapStacks(ItemStack stack){
        ItemStack cachedStack = getStack();
        setStack(stack);
        inventoryChanged();
        return cachedStack;
    }

    @Override
    public DefaultedList<ItemStack> getItems(){
        return items;
    }





  //saving/sync stuff
  public void updateInClientWorld() {
      ((ServerWorld) world).getChunkManager().markForUpdate(pos);
  }

    @Override
    public void inventoryChanged(){
        this.markDirty();
        if (world != null && !world.isClient){
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            updateInClientWorld();
        }

    }

    @Override
    public NbtCompound toInitialChunkDataNbt(){
        return createNbt();
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket(){
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
        readAdditionalData(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        storeAdditionalData(nbt);
    }

    public void storeAdditionalData(NbtCompound nbt){

    }

    public void readAdditionalData(NbtCompound nbt){

    }
}
