package aster.songweaver.registry.world.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class FractalFoliagePlacer extends FoliagePlacer {
    public static final Codec<FractalFoliagePlacer> CODEC =
            RecordCodecBuilder.create(instance ->
                    fillFoliagePlacerFields(instance)
                            .apply(instance, FractalFoliagePlacer::new));

    public FractalFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return LoomFoliagePlacers.FRACTAL_LEAF_PLACER;
    }



    @Override
    protected void generate(
            TestableWorld world,
            BlockPlacer placer,
            Random random,
            TreeFeatureConfig config,
            int trunkHeight,
            TreeNode treeNode,
            int foliageHeight,
            int radius,
            int offset) {

        BlockPos center = treeNode.getCenter().up(offset);
        int baseRadius = this.radius.get(random);


        generateBlob(world, placer, random, config, center, baseRadius);


        int smallRadius = Math.max(1, baseRadius - 1);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos offsetPos = center.offset(dir, baseRadius - 1);
            generateBlob(world, placer, random, config, offsetPos, smallRadius);
        }


        if (random.nextBoolean()) {
            generateBlob(world, placer, random, config, center.up(), smallRadius);
        }
    }

        private void generateBlob(
                TestableWorld world,
                BlockPlacer placer,
                Random random,
                TreeFeatureConfig config,
                BlockPos center,
        int radius) {

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -1; y <= 1; y++) {

                        double dist = x * x + z * z;

                        if (dist <= radius * radius) {

                            BlockPos leafPos = center.add(x, y, z);

                            if (TreeFeature.canReplace(world, leafPos)) {
                                double centerDist = Math.sqrt(x*x + z*z);

                                if (centerDist < radius * 0.6 && random.nextFloat() < 0.15f) {
                                    // hidden support log
                                    placeSupportLog(world, placer, random, config, leafPos);
                                } else {

                                    BlockState leafState =
                                            config.foliageProvider.get(random, leafPos);

                                    placer.placeBlock(leafPos, leafState);
                                }
                            }
                        }
                    }
                }
            }
        }


    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 2 + random.nextInt(2);
    }

    @Override
    protected boolean isInvalidForLeaves(
            Random random,
            int dx,
            int y,
            int dz,
            int radius,
            boolean giantTrunk) {

        int dist = dx * dx + dz * dz;

        if (dist > radius * radius) return true;

        // Random sparse interior pockets
        if (dist < radius * radius / 2 && random.nextFloat() < 0.1f)
            return true;

        return false;
    }

    private void placeSupportLog(
            TestableWorld world,
            BlockPlacer placer,
            Random random,
            TreeFeatureConfig config,
            BlockPos pos) {

        if (random.nextFloat() < 0.12f) { // ~12% chance
            if (TreeFeature.canReplace(world, pos)) {

                BlockState logState =
                        config.trunkProvider.get(random, pos);

                placer.placeBlock(pos, logState);
            }
        }
    }
}
