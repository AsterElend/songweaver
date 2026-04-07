package aster.songweaver.registry.physical.block;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomFluids;
import aster.songweaver.registry.physical.LoomItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class LetheanFluid extends FlowableFluid {
    @Override
    public Fluid getFlowing() {
        return LoomFluids.LETHEAN_WATER_FLOWING;
    }




    @Override
    public Fluid getStill() {
        return LoomFluids.LETHEAN_WATER;
    }

    @Override
    protected boolean isInfinite(World world) {
        return false;
    }



    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 4; // vanilla-ish
    }

    @Override
    public int getTickRate(WorldView view){
        return 5; // fine
    }
    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public Item getBucketItem() {
        return LoomItems.LETHEAN_WATER_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return !fluid.matchesType(this);
    }


    @Override
    protected float getBlastResistance() {
        return 100f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return (BlockState) LoomBlockStuff.LETHEAN_WATER.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

     public static class Still extends LetheanFluid {
        public Still() {
        }

        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isStill(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends LetheanFluid {
        public Flowing() {
        }

        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(new Property[]{LEVEL});

        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        public boolean isStill(FluidState state) {
            return false;
        }
    }

}
