package aster.songweaver.registry.physical.be;

import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RiftBlockEntity extends BlockEntity {
    public RiftBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.RIFT_BLOCK_ENTITY, pos, state);
    }
}
