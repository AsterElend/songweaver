package aster.songweaver.registry.physical.block;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomFluids;
import aster.songweaver.registry.physical.LoomItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
@SuppressWarnings("deprecation")
public abstract class LetheanFluid extends FlowableFluid {
    public LetheanFluid() {
    }


    public Fluid getFlowing() {
        return LoomFluids.LETHEAN_WATER_FLOWING;
    }

    public Fluid getStill() {
        return LoomFluids.LETHEAN_WATER_STATIC;
    }

    public Item getBucketItem() {
        return LoomItems.LETHEAN_WATER_BUCKET;
    }

    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!state.isStill() && !(Boolean)state.get(FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playSound((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), (double)0.0F, (double)0.0F, (double)0.0F);
        }

    }

    @Nullable
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_WATER;
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
    public int getFlowSpeed(WorldView world) {
        return 4;
    }
    @Override
    public BlockState toBlockState(FluidState state) {
        return LoomBlockStuff.LETHEAN_WATER_BLOCK.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }
    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == LoomFluids.LETHEAN_WATER_STATIC || fluid == LoomFluids.LETHEAN_WATER_FLOWING;
    }
    @Override
    public int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }
    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }
    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isIn(TagKey.of(RegistryKeys.FLUID, Songweaver.locate("lethean")));
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }
    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
    }

    public static class Still extends LetheanFluid {

        public Still() {
        }
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends LetheanFluid {
        public Flowing() {
        }
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }
        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }


}
