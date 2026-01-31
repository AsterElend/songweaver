package aster.songweaver.registry.physical.ritual;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
@SuppressWarnings("deprecation")
public class KhipuHookBlock extends WallMountedBlock implements BlockEntityProvider {

    public KhipuHookBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(FACE, WallMountLocation.WALL)
        );
    }

    /* ---------------- BLOCK ENTITY ---------------- */

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KhipuHookBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
       builder.add(FACING, FACE);
    }


    /* ---------------- PLACEMENT ---------------- */

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state == null) return null;

        // Reject floor / ceiling placements
        if (state.get(FACE) != WallMountLocation.WALL) {
            return null;
        }

        return state;
    }

    /* ---------------- INTERACTION ---------------- */

    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof KhipuHookBlockEntity hook)) {
            return ActionResult.PASS;
        }

        ItemStack held = player.getStackInHand(hand);

        if (!hook.hasItem() && !held.isEmpty()) {
            hook.setItem(held.split(1));
            return ActionResult.CONSUME;
        }

        if (hook.hasItem() && held.isEmpty()) {
            ItemStack extracted = hook.removeItem();
            if (!player.getInventory().insertStack(extracted)) {
                player.dropItem(extracted, false);
            }
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
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
        return SHAPES.get(state.get(WallMountedBlock.FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state,
                                        BlockView world,
                                        BlockPos pos,
                                        ShapeContext context) {
        return SHAPES.get(state.get(WallMountedBlock.FACING));
    }

    /* ---------------- ROTATION UTILS ---------------- */

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
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {

        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof KhipuHookBlockEntity inventory) {
                ItemScatterer.spawn(world, pos, inventory);
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}

