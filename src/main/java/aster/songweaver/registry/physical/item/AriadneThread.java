package aster.songweaver.registry.physical.item;

import aster.songweaver.api.DimensionTravel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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

        ItemStack stack = player.getStackInHand(hand);
        NbtCompound tag = stack.getNbt();

        if (tag == null) return TypedActionResult.fail(stack);

        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        Identifier dim = new Identifier(tag.getString("dim"));
        ServerWorld target = player.getServer().getWorld(
                RegistryKey.of(RegistryKeys.WORLD, dim)
        );

        if (target != null) {

            player.teleport(
                    target,
                    x + 0.5,
                    y,
                    z + 0.5,
                    Set.of(),
                    player.getYaw(),
                    player.getPitch()
            );

            DimensionTravel.leaveHighWilderness(player);

            stack.decrement(1);
        }

        return TypedActionResult.success(stack);
    }
}
