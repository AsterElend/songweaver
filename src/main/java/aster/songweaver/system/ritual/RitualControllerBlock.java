package aster.songweaver.system.ritual;

import aster.songweaver.registry.LoomMiscRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("deprecation")
public class RitualControllerBlock extends BlockWithEntity {

    public RitualControllerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    private static final VoxelShape SHAPE =
            Block.createCuboidShape(
                    0.0,  // minX
                    0.0,  // minY
                    0.0,  // minZ
                    16.0, // maxX
                    14.0, // maxY
                    16.0  // maxZ
            );


    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualControllerBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RitualControllerBlockEntity controller) {
                if (controller.cancelRitual(player)) {
                    return ActionResult.SUCCESS;
                }
            }
        }


        return ActionResult.PASS;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClient
                ? null
                : checkType(
                type,
                LoomMiscRegistry.RITUAL_ENTITY,
                (w, pos, s, be) -> be.tick()
        );
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
