package aster.songweaver.registry.physical;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
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
                        entries.add(LoomItems.PATTERN_BOOK);
                        entries.add(LoomItems.DISTAFF_BASIC);
                        entries.add(LoomItems.DISTAFF_IRON);
                        entries.add(LoomItems.DISTAFF_DIAMOND);
                        entries.add(LoomItems.DISTAFF_NETHERITE);
                        entries.add(LoomItems.DISTAFF_ASTRAL);
                        entries.add(LoomItems.BOBBIN_ITEM);
                        entries.add(LoomItems.CONTROLLER_ITEM);
                        entries.add(LoomItems.SPINDLE);
                        entries.add(LoomItems.STARSTONE_ITEM);
                        entries.add(LoomItems.TRANSIT_ITEM);
                        entries.add(LoomItems.MUSIC_STAND_ITEM);
                        entries.add(LoomItems.SHEET_MUSIC);
                        entries.add(LoomItems.KHIPU_HOOK_ITEM);
                        entries.add(LoomItems.KHIPU);

                    }).build());



}
