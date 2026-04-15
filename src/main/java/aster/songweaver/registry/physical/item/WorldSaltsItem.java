package aster.songweaver.registry.physical.item;

import aster.songweaver.cca.SongweaverComponents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class WorldSaltsItem extends Item {


    public WorldSaltsItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user){
        super.finishUsing(stack, world, user);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
            SongweaverComponents.FORGOTTEN.get(serverPlayer).rememberRandom(serverPlayer);

            if (serverPlayer.isCreative() && serverPlayer.isSneaking()){
                SongweaverComponents.FORGOTTEN.get(serverPlayer).reset();
            }
        }



        return stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack){
        return UseAction.DRINK;
    }

}
