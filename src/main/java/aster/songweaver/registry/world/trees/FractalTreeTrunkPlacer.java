package aster.songweaver.registry.world.trees;

import aster.songweaver.registry.physical.LoomBlockStuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FractalTreeTrunkPlacer extends TrunkPlacer {
    public static final Codec<FractalTreeTrunkPlacer> CODEC =
            RecordCodecBuilder.create(instance ->
                    fillTrunkPlacerFields(instance)
                            .apply(instance, FractalTreeTrunkPlacer::new));

    public FractalTreeTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return LoomTrunkPlacers.FRACTAL_TRUNK;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(
            TestableWorld world,
            BiConsumer<BlockPos, BlockState> replacer,
            Random random,
            int height,
            BlockPos startPos,
            TreeFeatureConfig config) {



        List<FoliagePlacer.TreeNode> foliageNodes = new ArrayList<>();
        Vec3d dir = new Vec3d(0, 1, 0); // straight up

        branch(world, replacer, random, startPos, dir, height, 0, foliageNodes, config);

        return foliageNodes;

        }

    private void branch(
            TestableWorld world,
            BiConsumer<BlockPos, BlockState> replacer,
            Random random,
            BlockPos start,
            Vec3d dir,
            int length,
            int depth,
            List<FoliagePlacer.TreeNode> foliage,
            TreeFeatureConfig config
    ) {

        if (depth > 6 || length < 2) {
            foliage.add(new FoliagePlacer.TreeNode(start, 2, false));
            return;
        }

        BlockPos.Mutable pos = start.mutableCopy();
        Vec3d currentDir = dir.normalize();

        for (int i = 0; i < length; i++) {

            if (TreeFeature.canReplace(world, pos)) {
                getAndSetState(world, replacer, random, pos, config);
            }

            pos.move(
                    (int)Math.round(currentDir.x),
                    (int)Math.round(currentDir.y),
                    (int)Math.round(currentDir.z)
            );

            // spiral curvature
            double spiralAngle = 0.25;

            currentDir = rotate(
                    currentDir,
                    new Vec3d(0,1,0),
                    spiralAngle
            ).normalize();
        }

        BlockPos end = pos.toImmutable();

        int newLength = (int)(length * 0.7);

        // two symmetric branches
        Vec3d axis = new Vec3d(1,0,0);

        Vec3d branchA = rotate(currentDir, axis, Math.PI / 4);
        Vec3d branchB = rotate(currentDir, axis, -Math.PI / 4);

        branch(world, replacer, random, end, branchA, newLength, depth + 1, foliage, config);
        branch(world, replacer, random, end, branchB, newLength, depth + 1, foliage, config);
    }

    private Vec3d rotate(Vec3d v, Vec3d axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return v.multiply(cos)
                .add(axis.crossProduct(v).multiply(sin))
                .add(axis.multiply(axis.dotProduct(v) * (1 - cos)));
    }

}
