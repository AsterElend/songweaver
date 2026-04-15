package aster.songweaver.registry.physical.item;

import aster.songweaver.cca.SongweaverComponents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class LetheanWaterBottle extends Item {

    public LetheanWaterBottle(Settings settings) {
        super(settings);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        super.finishUsing(stack, world, user);
        ServerPlayerEntity serverPlayer;

        if (user instanceof ServerPlayerEntity) {
            serverPlayer = (ServerPlayerEntity) user;
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
            SongweaverComponents.FORGOTTEN.get(serverPlayer).forgetRandom(serverPlayer);
        }



        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (user instanceof PlayerEntity playerEntity && !playerEntity.getAbilities().creativeMode) {
                ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                if (!playerEntity.getInventory().insertStack(itemStack)) {
                    playerEntity.dropItem(itemStack, false);
                }
            }

            return stack;
        }
    }
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }
}
