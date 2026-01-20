package aster.songweaver.system.spell.rituals;

import aster.songweaver.registry.LoomMultiblocks;
import aster.songweaver.registry.LoomTags;
import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.Ritual;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class TerraformRitual implements Ritual {

    @Override
    public void ritualCast(ServerWorld world, ServerPlayerEntity caster, RitualControllerBlockEntity loom, @Nullable JsonObject data) {

        vaporiseHemisphere(world, loom.getPos(), 16, Direction.UP, LoomTags.TERRAFORM_WHITELIST);

    }


    public static void vaporiseHemisphere(
            ServerWorld world,
            BlockPos center,
            int radius,
            Direction hemisphereDirection,
            TagKey<Block> whitelistTag
    ) {
        int rSq = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    // Sphere check
                    if (x * x + y * y + z * z > rSq) continue;

                    // Hemisphere check
                    if (!isInHemisphere(x, y, z, hemisphereDirection)) continue;

                    BlockPos pos = center.add(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    // Skip air
                    if (state.isAir()) continue;

                    // Tag whitelist check
                    if (!state.isIn(whitelistTag)) continue;

                    // Optional: hardness check (avoid bedrock-like stuff)
                    if (state.getHardness(world, pos) < 0) continue;

                    // Vaporise block
                    world.breakBlock(pos, false);

                    // Optional visual effect
                    world.spawnParticles(
                            ParticleTypes.CLOUD,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            6,
                            0.2, 0.2, 0.2,
                            0.02
                    );
                }
            }
        }
    }

    private static boolean isInHemisphere(int x, int y, int z, Direction dir) {
        return switch (dir) {
            case UP    -> y >= 0;
            case DOWN  -> y <= 0;
            case NORTH -> z <= 0;
            case SOUTH -> z >= 0;
            case WEST  -> x <= 0;
            case EAST  -> x >= 0;
        };
    }


}
