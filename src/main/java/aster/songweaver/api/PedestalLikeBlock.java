package aster.songweaver.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
@SuppressWarnings("deprecation")
public abstract class PedestalLikeBlock extends BlockWithEntity {
    protected PedestalLikeBlock(Settings settings) {
        super(settings);
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

        if (player.isSneaking()){
            doSneakInteractions(pedestal, player, hand);
            return ActionResult.SUCCESS;
        }

        ItemStack held = player.getStackInHand(hand);


       ItemStack mutatedHeld = pedestal.stackInteractionAttempt(held);
        if (!player.isCreative()) {
            player.setStackInHand(hand, mutatedHeld);
        }

        return ActionResult.CONSUME;
    }
    public void doSneakInteractions(PedestalLikeBlockEntity entity, PlayerEntity player, Hand hand) {
        // base no-op
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
