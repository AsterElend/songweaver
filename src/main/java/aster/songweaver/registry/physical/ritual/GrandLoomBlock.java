package aster.songweaver.registry.physical.ritual;

import aster.songweaver.registry.LoomTags;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Note;
import aster.songweaver.system.spell.definition.Ritual;
import aster.songweaver.system.spell.definition.RitualDefinition;
import aster.songweaver.system.spell.loaders.RitualReloadListener;
import aster.songweaver.util.TierCheckHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class GrandLoomBlock extends BlockWithEntity {

    @Override
    public boolean hasComparatorOutput(BlockState state){
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos){
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrandLoomBlockEntity controller) {
                if (controller.hasActiveRitual()){
                    return 15;
                }
            }
        }

        return 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean receivingRedstone = world.isReceivingRedstonePower(pos);
            if (receivingRedstone) {
                // Do something when it receives redstone
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof GrandLoomBlockEntity controller) {

                    MusicStandBlockEntity stand = controller.getMusicStand(pos);
                    List<Note> sequence = stand.getNotes();

                    if (stand == null || sequence == null){
                        SongServerCasting.sendFeedback(controller.lastCaster, CastFeedback.NO_STAND);
                        return;
                    }

                    RitualDefinition ritual = RitualReloadListener.matchForRitual(sequence);

                    if (ritual == null){
                        SongServerCasting.sendFeedback(controller.lastCaster, CastFeedback.UNKNOWN_SONG);
                        return;
                    }


                    controller.tryStartRitual(controller.lastCaster, ritual);


                }
            }
        }
    }

    public GrandLoomBlock(Settings settings) {
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
        return new GrandLoomBlockEntity(pos, state);
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
            if (be instanceof GrandLoomBlockEntity controller) {
                if (controller.cancelRitual(player)) {
                    return ActionResult.SUCCESS;
                } else {
                    controller.lastCaster = (ServerPlayerEntity) player;
                    ItemStack stack = player.getStackInHand(hand);
                    if (stack.isIn(LoomTags.DISTAFFS)){
                        controller.stockpiledTier = TierCheckHelper.getHeldTier((ServerPlayerEntity) player);
                    }
                    player.sendMessage(
                            Text.literal("Synced."),
                            true
                    );

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
