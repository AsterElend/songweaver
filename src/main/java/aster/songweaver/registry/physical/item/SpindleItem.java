package aster.songweaver.registry.physical.item;

import aster.songweaver.registry.NoteHolderItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static aster.songweaver.util.SpellUtil.tryClientCast;

public class SpindleItem extends NoteHolderItem {
    public SpindleItem(Settings settings) {
        super(settings);
    }
    public static final int COOLDOWN_TICKS = 40;




    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient || hand != Hand.MAIN_HAND) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if (tryClientCast(user, hand)) {
            return TypedActionResult.consume(user.getStackInHand(hand));
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }




}
