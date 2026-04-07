package aster.songweaver.registry.physical.be;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import aster.songweaver.registry.physical.item.KhipuItem;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class KhipuHookBlockEntity extends PedestalLikeBlockEntity {


    public KhipuHookBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.KHIPU_HOOK_ENTITY, pos, state);
    }



    public BlockPos getStoredPos() {
        if (items.isEmpty() || !(items.get(0).getItem() instanceof KhipuItem)) return null;

    return KhipuItem.getBlockPos(items.get(0));
    }

    public boolean hasStoredLocation(){

        return getStoredPos() != null;
    }


}
