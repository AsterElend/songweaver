package aster.songweaver.registry.physical.be;

import aster.songweaver.api.ImplementedInventory;
import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.item.KhipuItem;
import aster.songweaver.registry.physical.LoomBlockStuff;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class KhipuHookBlockEntity extends PedestalLikeBlockEntity {


    public KhipuHookBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockStuff.KHIPU_HOOK_ENTITY, pos, state);
    }


@Nullable
    public BlockPos getStoredPos() {
        if (items.isEmpty() || !(items.get(0).getItem() instanceof KhipuItem)) return null;

    return KhipuItem.getBlockPos(items.get(0));
    }

    public boolean hasItem() {
    return items.isEmpty();
    }


}
