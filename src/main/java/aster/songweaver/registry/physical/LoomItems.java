package aster.songweaver.registry.physical;

import aster.songweaver.Songweaver;
import aster.songweaver.api.NoteHolderItem;
import aster.songweaver.registry.physical.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class LoomItems {
    public static final List<Item> LOOM_ITEMS = new ArrayList<>();

    public static final Item DISTAFF_BASIC = registerItem("distaff_basic", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_IRON = registerItem("distaff_iron", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_DIAMOND = registerItem("distaff_diamond", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_NETHERITE = registerItem("distaff_netherite", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_ASTRAL = registerItem("distaff_astral", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item BAGUETTE_MAGIQUE = registerItem("baguette_magique", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item SPINDLE = registerItem("spindle", new SpindleItem(new FabricItemSettings().maxCount(1)));
    public static final Item PATTERN_BOOK = registerItem("book_of_patterns", new PatternBookItem(new FabricItemSettings().maxCount(1)));
    public static final Item KHIPU = registerItem("khipu", new KhipuItem(new FabricItemSettings().maxCount(1)));
    public static final Item TREE_TESTING_AXE = registerItem("testing_axe", new TreeTestingAxe(new FabricItemSettings().maxCount(1)));




    public static final Item SHEET_MUSIC = registerItem("sheet_music", new NoteHolderItem(new FabricItemSettings().maxCount(1)));
    public static final Item THREAD = registerItem("thread", new AriadneThread(new FabricItemSettings().maxCount(1)));
    public static final Item WARDING_PRISM = registerItem("warding_prism", new WardingPrismItem(new FabricItemSettings().maxCount(1)));
    public static final Item LETHEAN_WATER_BUCKET = registerItem("lethean_water_bucket", new BucketItem(LoomFluids.LETHEAN_WATER_STATIC,
            new FabricItemSettings().maxCount(1)));
    public static final Item MUSIC_BOX = registerItem("music_box", new MusicBoxItem(new FabricItemSettings().maxCount(1)));
    public static final Item LETHEAN_WATER_BOTTLE = registerItem("lethean_water_bottle", new LetheanWaterBottle(new FabricItemSettings().maxCount(16)));
    public static final Item WORLD_SALTS = registerItem("world_salts", new WorldSaltsItem(new FabricItemSettings().maxCount(16)));






    public static void registerItems(){
        LOOM_ITEMS.remove(TREE_TESTING_AXE);
        Songweaver.LOGGER.info("Registering Items for Loom");
    }

    public static Item registerItem(String name, Item item) {
        LOOM_ITEMS.add(item);
        return Registry.register(Registries.ITEM, new Identifier(Songweaver.MOD_ID, name), item); }

}
