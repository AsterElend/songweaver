package aster.songweaver.registry.physical.block;

import aster.songweaver.api.PedestalLikeBlock;
import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.be.BobbinBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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

    @Override
    public void doSneakInteractions(PedestalLikeBlockEntity entity, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof DyeItem dye && entity instanceof BobbinBlockEntity bobbin) {
            bobbin.setColor(dye.getColor().getId());
        }
    }
}
