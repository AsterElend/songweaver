package aster.songweaver.registry.physical.block;

import aster.songweaver.api.PedestalLikeBlock;
import aster.songweaver.registry.physical.be.MusicStandBlockEntity;
import aster.songweaver.util.VoxelShapeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MusicStandBlock extends PedestalLikeBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    // NORTH-facing canonical shape
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3, 0, 3, 13, 2, 13),   // base
            Block.createCuboidShape(7, 2, 7, 9, 25, 9),    // pole
            Block.createCuboidShape(2, 20, 3, 14, 21, 5),  // ledge
            Block.createCuboidShape(2, 20, 5, 14, 30, 7)   // plate
    );

    private static final Map<Direction, VoxelShape> SHAPES =
            Arrays.stream(Direction.Type.HORIZONTAL.stream().toArray(Direction[]::new))
                    .collect(Collectors.toMap(
                            d -> d,
                            d -> VoxelShapeUtil.rotate(NORTH_SHAPE, Direction.NORTH, d)
                    ));




    public MusicStandBlock(Settings settings) {
        super(settings);
        setDefaultState(
                getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();

        if (pos.getY() >= world.getTopY() - 1) return null;
        if (!world.getBlockState(pos.up()).canReplace(ctx)) return null;

        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(),
                state.with(HALF, DoubleBlockHalf.UPPER),
                Block.NOTIFY_ALL
        );
    }



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockPos upperPos = state.get(HALF) == DoubleBlockHalf.UPPER
                    ? pos
                    : pos.up();

            BlockEntity be = world.getBlockEntity(upperPos);
            if (be instanceof MusicStandBlockEntity stand) {
                ItemScatterer.spawn(world, upperPos, stand);
            }

            world.breakBlock(upperPos, false);
            world.breakBlock(upperPos.down(), false);
        }

        super.onBreak(world, pos, state, player);
    }

    private static VoxelShape offsetDown(VoxelShape shape) {
        return shape.offset(0, -1.0, 0);
    }


    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockView world,
            BlockPos pos,
            ShapeContext context
    ) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            return VoxelShapes.empty(); // 👈 no collision
        }

        return SHAPES.get(state.get(FACING));
    }



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {

        VoxelShape shape = SHAPES.get(state.get(FACING)); // canonical north-oriented shape

        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            // Shift the shape down by 1 so it visually matches the lower block
            return offsetDown(shape);
        }

        return shape; // lower block: normal
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.UPPER
                ? new MusicStandBlockEntity(pos, state)
                : null;
    }



}
