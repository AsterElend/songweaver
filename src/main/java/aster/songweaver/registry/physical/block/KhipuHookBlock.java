package aster.songweaver.registry.physical.block;

import aster.songweaver.api.PedestalLikeBlock;
import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.be.KhipuHookBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Properties;

@SuppressWarnings("deprecation")public class KhipuHookBlock extends WallMountedBlock implements BlockEntityProvider {

    public KhipuHookBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(FACE, WallMountLocation.WALL)
        );
    }

    /* ---------------- BLOCK ENTITY ---------------- */

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KhipuHookBlockEntity(pos, state);
    }

    /* ---------------- BLOCKSTATE ---------------- */

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    /* ---------------- PLACEMENT ---------------- */

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {

        for (Direction direction : ctx.getPlacementDirections()) {

            WallMountLocation face = direction.getAxis() == Direction.Axis.Y
                    ? WallMountLocation.FLOOR
                    : WallMountLocation.WALL;

            Direction facing = direction.getAxis() == Direction.Axis.Y
                    ? ctx.getHorizontalPlayerFacing()
                    : direction.getOpposite();

            BlockState state = this.getDefaultState()
                    .with(FACE, face)
                    .with(FACING, facing);

            if (face == WallMountLocation.WALL &&
                    state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {

                return state;
            }
        }

        return null;
    }

    /* ---------------- SHAPES ---------------- */

    private static final VoxelShape NORTH_SHAPE =
            Block.createCuboidShape(6, 4, 14, 10, 12, 16);

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, NORTH_SHAPE,
            Direction.SOUTH, rotateY(NORTH_SHAPE, 180),
            Direction.WEST,  rotateY(NORTH_SHAPE, 270),
            Direction.EAST,  rotateY(NORTH_SHAPE, 90)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state,
                                      BlockView world,
                                      BlockPos pos,
                                      ShapeContext context) {

        if (state.get(FACE) != WallMountLocation.WALL) {
            return VoxelShapes.empty();
        }

        return SHAPES.getOrDefault(state.get(FACING), NORTH_SHAPE);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state,
                                        BlockView world,
                                        BlockPos pos,
                                        ShapeContext context) {

        if (state.get(FACE) != WallMountLocation.WALL) {
            return VoxelShapes.empty();
        }

        return SHAPES.getOrDefault(state.get(FACING), NORTH_SHAPE);
    }

    /* ---------------- ROTATION ---------------- */

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    /* ---------------- SHAPE ROTATION ---------------- */

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

    //because of the cursed way that extend works, copy the methods from PedestalLikeBlock
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