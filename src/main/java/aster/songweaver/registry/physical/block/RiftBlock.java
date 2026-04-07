package aster.songweaver.registry.physical.block;

import aster.songweaver.registry.SongweaverParticles;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import aster.songweaver.registry.physical.LoomBlockStuff;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class RiftBlock extends BlockWithEntity {

    public RiftBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            teleport(serverPlayer);
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextFloat() < 0.7f) {
            Vec3d dir = new Vec3d(
                    random.nextDouble() - 0.5,
                    random.nextDouble() - 0.5,
                    random.nextDouble() - 0.5
            ).normalize().multiply(0.1);

            world.addParticle(
                    SongweaverParticles.SILK_PARTICLE,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    dir.x, dir.y, dir.z
            );


        }
    }
    private void teleport(ServerPlayerEntity player) {

        MinecraftServer server = player.getServer();
        if (server == null) return;

        RegistryKey<World> targetKey =
                RegistryKey.of(RegistryKeys.WORLD, new Identifier("songweaver", "high_wilderness"));

        ServerWorld targetWorld = server.getWorld(targetKey);
        if (targetWorld == null) return;

        // Toggle behavior (like nether portal)
        if (player.getServerWorld() == targetWorld) {
            targetWorld = server.getOverworld();
        }

        double x = player.getX();
        double z = player.getZ();

        // Ensure chunk is loaded
        targetWorld.getChunkManager().getChunk(
                (int)x >> 4,
                (int)z >> 4
        );

        // Find top solid surface (ignores leaves & air)
        int y = targetWorld.getTopY(
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                (int)Math.floor(x),
                (int)Math.floor(z)
        );

        BlockPos spawnPos = new BlockPos(
                (int)Math.floor(x),
                y,
                (int)Math.floor(z)
        );

        // If terrain is missing or void world
        if (y <= targetWorld.getBottomY()) {
            spawnPos = createSafetyPlatform(targetWorld, spawnPos);
        }

        player.teleport(
                targetWorld,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );


    }

    private BlockPos createSafetyPlatform(ServerWorld world, BlockPos center) {

        BlockPos platformPos = new BlockPos(center.getX(), 80, center.getZ());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos placePos = platformPos.add(dx, -1, dz);
                world.setBlockState(placePos,
                        LoomBlockStuff.VOIDSTONE.getDefaultState());
            }
        }

        return platformPos;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return LoomBlockEntities.RIFT_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
