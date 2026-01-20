package aster.songweaver.registry.items;

import aster.songweaver.Songweaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

public class PatternBookItem extends Item {

    public static Identifier PATTERN_BOOK_ID = Songweaver.locate("book_of_patterns");

    public PatternBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (user instanceof ServerPlayerEntity player){
            PatchouliAPI.get().openBookGUI(player, PATTERN_BOOK_ID);
            ItemStack stack = player.getStackInHand(hand);
            return TypedActionResult.success(stack, false);
        }

       return TypedActionResult.fail(user.getStackInHand(hand));


    }


}
