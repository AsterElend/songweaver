package aster.songweaver.registry.physical;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransitRelay extends Block {

    public TransitRelay(Settings settings) {
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

    private void teleport(ServerPlayerEntity player) {
        ServerWorld targetWorld = player.getServer()
                .getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier("songweaver", "high_wilderness")));

        if (targetWorld == null) return;

        player.teleport(
                targetWorld,
                player.getX(),
                80,
                player.getZ(),
                player.getYaw(),
                player.getPitch()
        );
    }
}
