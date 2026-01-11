package aster.songweaver.ritual;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BobbinBlock extends BlockWithEntity {


    public BobbinBlock(Settings settings) {
        super(settings);
    }

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
            0.25, 0.0, 0.25,  // x1, y1, z1
            0.75, 1.0, 0.75   // x2, y2, z2
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }



    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BobbinBlockEntity(pos, state);
    }
    @Override
    public ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof BobbinBlockEntity bobbin)) {
            return ActionResult.PASS;
        }

        ItemStack held = player.getStackInHand(hand);

        // Insert
        if (!held.isEmpty()) {
            return bobbin.insertFromPlayer(player, hand)
                    ? ActionResult.CONSUME
                    : ActionResult.PASS;
        }

        // Extract
        return bobbin.extractToPlayer(player, player.isSneaking())
                ? ActionResult.CONSUME
                : ActionResult.PASS;
    }




}
