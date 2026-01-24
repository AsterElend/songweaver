package aster.songweaver.registry.physical.ritual;

import aster.songweaver.util.VoxelShapeUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
@SuppressWarnings("deprecation")
public class MusicStandBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    // Base + lower pole
    private static final VoxelShape LOWER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3, 0, 3, 13, 2, 13), // base
            Block.createCuboidShape(7, 2, 9, 9, 16, 9)   // lower pole
    );

    // Upper pole + sheet
    private static final VoxelShape UPPER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(7, 0, 7, 9, 14, 9),   // upper pole (height 14 relative to upper block)
            Block.createCuboidShape(9, 4, 2, 11, 14, 14)  // sheet part (relative to upper block)
    );


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
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        // Redirect clicks on upper block to lower block
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.down();
            state = world.getBlockState(pos);
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof MusicStandBlockEntity stand)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);

        if (!world.isClient) {
            // insert
            if (!held.isEmpty() && stand.getStack().isEmpty()) {
                stand.setStack(held.split(1));
                return ActionResult.CONSUME;
            }

            // extract
            if (held.isEmpty() && !stand.getStack().isEmpty()) {
                player.setStackInHand(hand, stand.removeStack());
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            // Always break the lower block
            if (state.get(HALF) == DoubleBlockHalf.UPPER) {
                pos = pos.down();
                state = world.getBlockState(pos);
            }

            if (state.isOf(this)) {
                world.breakBlock(pos, true, player);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return state.get(HALF) == DoubleBlockHalf.UPPER
                ? VoxelShapes.empty() // Upper block is non-solid
                : LOWER_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            // Rotate the upper shape according to FACING
            return VoxelShapeUtil.rotate(UPPER_SHAPE, state.get(FACING));
        }
        // Lower shape stays unrotated if symmetric; rotate if asymmetric
        return LOWER_SHAPE;
    }




    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // Only create a BE for the lower block
        return state.get(HALF) == DoubleBlockHalf.LOWER
                ? new MusicStandBlockEntity(pos, state)
                : null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }



}
