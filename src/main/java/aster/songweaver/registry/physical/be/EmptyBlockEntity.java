package aster.songweaver.registry.physical.be;

import aster.songweaver.registry.physical.LoomBlockStuff;
import net.minecraft.block.BlockState;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class EmptyBlockEntity extends BlockEntity {
    public EmptyBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockStuff.BLANK_BLOCK_ENTITY, pos, state);
    }
}
