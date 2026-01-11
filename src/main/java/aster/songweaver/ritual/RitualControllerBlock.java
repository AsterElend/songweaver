package aster.songweaver.ritual;

import aster.songweaver.registry.LoomMiscRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("deprecation")
public class RitualControllerBlock extends BlockWithEntity {

    public RitualControllerBlock(Settings settings) {
        super(settings);
    }

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




}
