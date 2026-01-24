package aster.songweaver.registry.physical;


import aster.songweaver.client.InputBuffer;
import aster.songweaver.util.SpellUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Distaff extends Item {

    public Distaff(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient || hand != Hand.MAIN_HAND) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if (SpellUtil.tryClientCast(user, hand)) {
            return TypedActionResult.consume(user.getStackInHand(hand));
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }


}



