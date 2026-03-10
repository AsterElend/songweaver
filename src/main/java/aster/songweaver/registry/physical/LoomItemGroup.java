package aster.songweaver.registry.physical;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LoomItemGroup {


    public static void init(){

    }


    public static final ItemGroup SONGWEAVER_TAB = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier("songweaver", "creative_tab"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.songweaver.creative_tab"))
                    .icon(() -> new ItemStack(LoomItems.PATTERN_BOOK))
                    .entries((displayContext, entries) -> {
                        for (Item item : LoomItems.LOOM_ITEMS){
                            entries.add(item);
                        }
                        for (Block block: LoomBlockStuff.LOOM_BLOCKS){
                            entries.add(block);
                        }

                    }).build());



}
