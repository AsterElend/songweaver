package aster.songweaver.registry.physical.ritual;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("deprecation")
public class BobbinBlock extends BlockWithEntity {

    public BobbinBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BobbinBlockEntity(pos, state);
    }

    /* ───────────────────────── Right-Click Interaction ───────────────────────── */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof BobbinBlockEntity bobbin)) {
            return ActionResult.PASS;
        }

        ItemStack held = player.getStackInHand(hand);
        ItemStack slot = bobbin.getStack();

        // ----- 1️⃣ Extraction: player hand empty -----
        if (held.isEmpty()) {
            if (slot.isEmpty()) {
                return ActionResult.PASS;
            }

            ItemStack extracted = slot.copy();
            bobbin.clearStack(0);

            if (!player.getInventory().insertStack(extracted)) {
                player.dropItem(extracted, false);
            }

            return ActionResult.CONSUME;
        }

        // ----- 2️⃣ Insertion: bobbin empty -----
        if (slot.isEmpty()) {
            bobbin.setStack(0, held.copy());
            held.setCount(0);
            return ActionResult.CONSUME;
        }

        // ----- 3️⃣ Combine stacks -----
        if (ItemStack.canCombine(slot, held)) {
            int space = slot.getMaxCount() - slot.getCount();
            if (space <= 0) {
                return ActionResult.PASS;
            }

            int moved = Math.min(space, held.getCount());

            ItemStack newStack = slot.copy();
            newStack.increment(moved);
            bobbin.setStack(0, newStack);

            held.decrement(moved);
            return ActionResult.CONSUME;
        }

        // ----- 4️⃣ Swap stacks (NEW) -----
        ItemStack slotCopy = slot.copy();
        bobbin.setStack(0, held.copy());
        player.setStackInHand(hand, slotCopy);

        return ActionResult.CONSUME;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    public static VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 16,13);
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {

        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof BobbinBlockEntity inventory) {
                ItemScatterer.spawn(world, pos, inventory);
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
