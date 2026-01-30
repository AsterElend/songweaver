package aster.songweaver.registry.physical;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
@SuppressWarnings("deprecation")
public class LightOrbBlock extends Block {
    public LightOrbBlock(Settings settings) {
        super(settings);
    }
    public VoxelShape SHAPE = Block.createCuboidShape(4, 4, 4, 12, 12, 12); // 8x8x8 cube in center

    // Smaller-than-full-block shape, centered
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return SHAPE;
    }
    // Client-side particles
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.01, 0);
        }
    }



    // Optional: make it replaceable (so it can disappear if needed)
    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

}
