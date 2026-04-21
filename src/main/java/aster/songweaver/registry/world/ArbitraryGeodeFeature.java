package aster.songweaver.registry.world;

import aster.songweaver.registry.LoomTags;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
public class ArbitraryGeodeFeature extends Feature<ArbitraryGeodeFeatureConfig> {

    private static final float INNER_RADIUS       = 4.5f;
    private static final float INNER_SHELL_RADIUS = 5.5f;
    private static final float OUTER_SHELL_RADIUS = 6.5f;
    private static final float INNER_BLOCK_SHELL_THICKNESS = 1.2f;
    public ArbitraryGeodeFeature(Codec<ArbitraryGeodeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<ArbitraryGeodeFeatureConfig> ctx) {
        ArbitraryGeodeFeatureConfig config = ctx.getConfig();
        WorldAccess world  = ctx.getWorld();
        BlockPos    origin = ctx.getOrigin();
        Random      random = ctx.getRandom();

        double noiseX = random.nextDouble() * 2.0 - 1.0;
        double noiseY = random.nextDouble() * 2.0 - 1.0;
        double noiseZ = random.nextDouble() * 2.0 - 1.0;

        int range = (int) OUTER_SHELL_RADIUS + 1;

        // ── 1. Shell pass ─────────────────────────────────────────────────────
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    double dist = distorted(dx, dy * 1.2, dz, noiseX, noiseY, noiseZ);
                    BlockPos pos = origin.add(dx, dy, dz);

                    if (dist <= INNER_SHELL_RADIUS) {
                        if (canReplace(world, pos))
                            world.setBlockState(pos, config.innerShellBlock(), 3);

                    } else if (dist <= OUTER_SHELL_RADIUS) {
                        if (canReplace(world, pos))
                            world.setBlockState(pos, config.outerShellBlock(), 3);
                    }
                }
            }
        }
        // tweak to taste

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    double dist = distorted(dx, dy * 1.2, dz, noiseX, noiseY, noiseZ);
                    if (dist > INNER_RADIUS) continue;

                    BlockPos pos = origin.add(dx, dy, dz);

                    if (dist > INNER_RADIUS - INNER_BLOCK_SHELL_THICKNESS) {
                        // Surface ring — amethyst / budding blocks
                        world.setBlockState(pos,
                                random.nextInt(12) == 0
                                        ? config.buddingBlock()
                                        : config.innerBlock(), 3);
                    } else {
                        // True interior — hollow it out
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }

        return true;
    }

    private static double distorted(double dx, double dy, double dz,
                                    double nx, double ny, double nz) {
        return Math.sqrt(
                (dx - nx) * (dx - nx) +
                        (dy - ny) * (dy - ny) +
                        (dz - nz) * (dz - nz)
        );
    }

    private static boolean canReplace(WorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.isIn(LoomTags.TERRAFORM_WHITELIST);
    }
}