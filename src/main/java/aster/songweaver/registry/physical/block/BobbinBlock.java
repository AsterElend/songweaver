package aster.songweaver.registry.physical.block;

import aster.songweaver.api.PedestalLikeBlock;
import aster.songweaver.registry.physical.be.BobbinBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("deprecation")
public class BobbinBlock extends PedestalLikeBlock {

    public BobbinBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BobbinBlockEntity(pos, state);
    }
    

    public static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 16,13);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
  
}
