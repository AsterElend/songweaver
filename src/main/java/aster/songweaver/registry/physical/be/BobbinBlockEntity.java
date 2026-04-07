package aster.songweaver.registry.physical.be;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BobbinBlockEntity extends PedestalLikeBlockEntity {
    
    public BobbinBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.BOBBIN_ENTITY, pos, state);
    }

    public int removeCount(int amount) {
        ItemStack removed = Inventories.splitStack(items, 0, amount);
        if (!removed.isEmpty()) {
            inventoryChanged();
        }
        return removed.getCount();
    }


}
