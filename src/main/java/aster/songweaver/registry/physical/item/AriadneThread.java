package aster.songweaver.registry.physical.item;

import aster.songweaver.registry.dimension.LoomDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Set;

public class AriadneThread extends Item {

    public AriadneThread(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient ) return TypedActionResult.success(player.getStackInHand(hand));
        World highWilderness = player.getServer().getWorld(LoomDimensions.HIGH_WILDERNESS);
        ItemStack stack = player.getStackInHand(hand);

        if (highWilderness == null) return TypedActionResult.success(player.getStackInHand(hand));

        if (world != player.getServer().getWorld(LoomDimensions.HIGH_WILDERNESS)){
            stack.decrement(1);
            return TypedActionResult.success(stack);
        }

        NbtCompound tag = stack.getNbt();

        if (tag == null) return TypedActionResult.fail(stack);


        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        Identifier dim = Identifier.tryParse(tag.getString("dim"));
        if (dim == null) return TypedActionResult.fail(stack);

        ServerWorld target = serverPlayer.getServer().getWorld(
                RegistryKey.of(RegistryKeys.WORLD, dim)
        );

        if (target != null) {

            serverPlayer.teleport(
                    target,
                    x + 0.5,
                    y,
                    z + 0.5,
                    Set.of(),
                    player.getYaw(),
                    player.getPitch()
            );


        }
        stack.decrement(1);
        return TypedActionResult.success(stack, false);
    }
}
