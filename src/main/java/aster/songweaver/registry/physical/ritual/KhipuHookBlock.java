package aster.songweaver.registry.physical.ritual;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
@SuppressWarnings("deprecation")
public class KhipuHookBlock extends HorizontalFacingBlock implements BlockEntityProvider {


    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public KhipuHookBlock(Settings settings) {
        super(settings);
        setDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
        );
    }

    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof KhipuHookBlockEntity hook)) {
            return ActionResult.PASS;
        }

        ItemStack held = player.getStackInHand(hand);

        // INSERT
        if (!hook.hasItem() && !held.isEmpty()) {
            ItemStack toInsert = held.copyWithCount(1);
            hook.setItem(toInsert);

            held.decrement(1);
            return ActionResult.CONSUME;
        }

        // EXTRACT
        if (hook.hasItem() && held.isEmpty()) {
            ItemStack extracted = hook.removeItem();

            if (!player.getInventory().insertStack(extracted)) {
                player.dropItem(extracted, false);
            }

            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KhipuHookBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction clickedSide = ctx.getSide();

        // First, try the clicked face if it's horizontal
        if (clickedSide.getAxis().isHorizontal()) {
            BlockState state = getDefaultState().with(FACING, clickedSide.getOpposite());
            if (state.canPlaceAt(world, pos)) return state;
        }

        // Then try all horizontal directions around the player
        Direction playerFacing = ctx.getHorizontalPlayerFacing();
        Direction[] horizontalDirs = new Direction[]{
                playerFacing,
                playerFacing.rotateYClockwise(),
                playerFacing.rotateYCounterclockwise(),
                playerFacing.getOpposite()
        };

        for (Direction dir : horizontalDirs) {
            BlockState state = getDefaultState().with(FACING, dir.getOpposite());
            if (state.canPlaceAt(world, pos)) return state;
        }

        // Nowhere to place
        return null;
    }



    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());

        return world.getBlockState(supportPos)
                .isSideSolidFullSquare(world, supportPos, facing);
    }


    @Override
    public void neighborUpdate(BlockState state,
                               World world,
                               BlockPos pos,
                               Block sourceBlock,
                               BlockPos sourcePos,
                               boolean notify) {

        if (!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, true);
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }


    @Override
    public BlockState getStateForNeighborUpdate(BlockState state,
                                                Direction direction,
                                                BlockState neighborState,
                                                WorldAccess world,
                                                BlockPos pos,
                                                BlockPos neighborPos) {

        if (direction == state.get(FACING).getOpposite()
                && !canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
    }

    private static final VoxelShape NORTH_SHAPE =
            Block.createCuboidShape(
                    6.0, 4.0, 14.0,
                    10.0, 12.0, 16.0
            );

    private static final Map<Direction, VoxelShape> SHAPES =
            Map.of(
                    Direction.NORTH, NORTH_SHAPE,
                    Direction.SOUTH, rotateY(NORTH_SHAPE, 180),
                    Direction.WEST,  rotateY(NORTH_SHAPE, 270),
                    Direction.EAST,  rotateY(NORTH_SHAPE, 90)
            );

    private static VoxelShape rotateY(VoxelShape shape, int degrees) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (degrees / 90) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = VoxelShapes.union(
                        buffer[1],
                        Block.createCuboidShape(
                                16 - maxZ, minY, minX,
                                16 - minZ, maxY, maxX
                        )
                );
            });
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }




    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state,
                                      BlockView world,
                                      BlockPos pos,
                                      ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state,
                                        BlockView world,
                                        BlockPos pos,
                                        ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }


}
