package aster.songweaver.registry.physical.block;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.be.KhipuHookBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")

public class KhipuHookBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final DirectionProperty FACING;

    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        SOUTH_SHAPE = Block.createCuboidShape(5.0F, 0.0F, 10.0F, 11.0F, 10.0F, 16.0F);
        NORTH_SHAPE = Block.createCuboidShape(5.0F, 0.0F, 0.0F, 11.0F, 10.0F, 6.0F);
        EAST_SHAPE = Block.createCuboidShape(10.0F, 0.0F, 5.0F, 16.0F, 10.0F, 11.0F);
        WEST_SHAPE = Block.createCuboidShape(0.0F, 0.0F, 5.0F, 6.0F, 10.0F, 11.0F);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public KhipuHookBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
        );
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case WEST -> EAST_SHAPE;
            case SOUTH -> NORTH_SHAPE;
            case NORTH -> SOUTH_SHAPE;
            default -> WEST_SHAPE;
        };
    }

  

    


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KhipuHookBlockEntity(pos, state);
    }
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
    }



    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState().with(FACING, Direction.NORTH);
        WorldView worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction[] directions = ctx.getPlacementDirections();

        for(Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.with(FACING, direction2);
                if (blockState.canPlaceAt(worldView, blockPos)) {
                    return blockState;
                }
            }
        }

        return null;
    }




    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }



    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof PedestalLikeBlockEntity pedestal)) {
            return ActionResult.PASS;
        }

        ItemStack held = player.getStackInHand(hand);


        ItemStack mutatedHeld = pedestal.stackInteractionAttempt(held);

        player.setStackInHand(hand, mutatedHeld);


        return ActionResult.CONSUME;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            scatterContents(world, pos);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static void scatterContents(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory inventory) {
            ItemScatterer.spawn(world, pos, inventory);
            world.updateComparators(pos, block);
            if (inventory instanceof PedestalLikeBlockEntity inWorldInteractionBlockEntity) {
                inWorldInteractionBlockEntity.inventoryChanged();
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}