package aster.songweaver.registry.physical.block;

import aster.songweaver.api.cast.SongweaverPackets;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Note;
import aster.songweaver.api.weaving.RitualDefinition;
import aster.songweaver.api.weaving.loaders.RitualReloadListener;
import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.LoomTags;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.registry.physical.be.MusicStandBlockEntity;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import aster.songweaver.util.TierCheckHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")public class GrandLoomBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public GrandLoomBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // --- Everything below is unchanged from your original ---

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof GrandLoomBlockEntity entity && placer.isPlayer()) {
            entity.redstonePlayer = placer.getUuid();
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrandLoomBlockEntity controller) {
                if (controller.hasActiveRitual()) {
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
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof GrandLoomBlockEntity controller) {
                    ServerPlayerEntity you = controller.getRedstoneOrClosestPlayer((ServerWorld) world);
                    if (you == null || SongweaverComponents.SILENCE.get(you).isSilenced()) return;

                    MusicStandBlockEntity stand = controller.getMusicStand(pos);
                    if (stand == null) {
                        SongweaverPackets.sendFeedback(you, CastFeedback.NO_STAND);
                        return;
                    }
                    List<Note> sequence = stand.getNotes();

                    if (sequence == null) {
                        SongweaverPackets.sendFeedback(you, CastFeedback.NO_STAND);
                        return;
                    }

                    RitualDefinition ritual = RitualReloadListener.matchForRitual(sequence);

                    if (ritual == null) {
                        SongweaverPackets.sendFeedback(you, CastFeedback.UNKNOWN_SONG);
                        return;
                    }

                    controller.tryStartRitual(you, ritual);
                }
            }
        }
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
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrandLoomBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrandLoomBlockEntity controller) {
                if (controller.cancelRitual()) {
                    return ActionResult.SUCCESS;
                } else {
                    ItemStack stack = player.getStackInHand(hand);
                    if (stack.isIn(LoomTags.DISTAFFS)) {
                        controller.stockpiledTier = TierCheckHelper.getHeldTier((ServerPlayerEntity) player);
                    }
                    player.sendMessage(Text.literal("Synced."), true);
                    controller.redstonePlayer = player.getUuid();
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient
                ? null
                : checkType(type, LoomBlockEntities.GRAND_LOOM_BLOCK_ENTITY,
                (w, pos, s, be) -> be.tick());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
