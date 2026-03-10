package aster.songweaver.registry.world;

import aster.songweaver.registry.LoomTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;

public class ArbitraryGeodeFeature
        extends Feature<ArbitraryGeodeFeatureConfig> {

    public ArbitraryGeodeFeature() {
        super(ArbitraryGeodeFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<ArbitraryGeodeFeatureConfig> context) {

        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        ArbitraryGeodeFeatureConfig config = context.getConfig();

        int distributionPoints = 3 + random.nextInt(3);
        int outerWallDistance = 4;
        int middleWallDistance = 3;
        int innerWallDistance = 2;
        int crackChance = 5;

        List<BlockPos> points = new ArrayList<>();

        for (int i = 0; i < distributionPoints; ++i) {
            points.add(origin.add(
                    random.nextInt(8) - 4,
                    random.nextInt(8) - 4,
                    random.nextInt(8) - 4
            ));
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int radius = 10;

        // ===============================
        // Main Geode Body Placement
        // ===============================

        for (BlockPos pos : BlockPos.iterate(
                origin.add(-radius, -radius, -radius),
                origin.add(radius, radius, radius))) {

            // 🔥 Whitelist protection
            if (!world.getBlockState(pos).isIn(LoomTags.TERRAFORM_WHITELIST)
                    && !world.getBlockState(pos).isAir()) {
                continue;
            }

            double density = 0.0D;

            for (BlockPos point : points) {
                density += 1.0D / point.getSquaredDistance(pos);
            }

            mutable.set(pos);

            if (density >= 1.0D / outerWallDistance) {

                world.setBlockState(
                        mutable,
                        Blocks.SMOOTH_BASALT.getDefaultState(),
                        Block.NOTIFY_LISTENERS
                );

            } else if (density >= 1.0D / middleWallDistance) {

                world.setBlockState(
                        mutable,
                        Blocks.CALCITE.getDefaultState(),
                        Block.NOTIFY_LISTENERS
                );

            } else if (density >= 1.0D / innerWallDistance) {

                world.setBlockState(
                        mutable,
                        config.innerBlock(),
                        Block.NOTIFY_LISTENERS
                );

            } else {

                world.setBlockState(
                        mutable,
                        Blocks.AIR.getDefaultState(),
                        Block.NOTIFY_LISTENERS
                );
            }
        }

        // ===============================
        // Crack Generation (Vanilla-Like)
        // ===============================

        if (random.nextInt(crackChance) == 0) {

            Direction crackDirection = Direction.random(random);

            for (BlockPos pos : BlockPos.iterate(
                    origin.add(-radius, -radius, -radius),
                    origin.add(radius, radius, radius))) {

                if (!world.getBlockState(pos).isIn(LoomTags.TERRAFORM_WHITELIST)
                        && !world.getBlockState(pos).isAir()) {
                    continue;
                }

                if (pos.getManhattanDistance(origin) < 4) {

                    world.setBlockState(
                            pos.offset(crackDirection),
                            Blocks.AIR.getDefaultState(),
                            Block.NOTIFY_LISTENERS
                    );
                }
            }
        }

        // ===============================
        // Budding Placement Pass
        // ===============================

        for (BlockPos pos : BlockPos.iterate(
                origin.add(-6, -6, -6),
                origin.add(6, 6, 6))) {

            if (world.getBlockState(pos).equals(config.innerBlock())
                    && random.nextFloat() < 0.08f) {

                world.setBlockState(
                        pos,
                        config.buddingBlock(),
                        Block.NOTIFY_LISTENERS
                );
            }
        }

        return true;
    }


}