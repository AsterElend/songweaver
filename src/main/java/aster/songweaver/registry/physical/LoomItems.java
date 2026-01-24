package aster.songweaver.registry.physical;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.NoteHolderItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static aster.songweaver.Songweaver.MOD_ID;
import static aster.songweaver.registry.physical.LoomMiscRegistry.*;

public class LoomItems {
    public static final Item DISTAFF_BASIC = registerItem("distaff_basic", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_IRON = registerItem("distaff_iron", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_DIAMOND = registerItem("distaff_diamond", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_NETHERITE = registerItem("distaff_netherite", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_ASTRAL = registerItem("distaff_astral", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item BAGUETTE_MAGIQUE = registerItem("baguette_magique", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item SPINDLE = registerItem("spindle", new SpindleItem(new FabricItemSettings().maxCount(1)));
    public static final Item PATTERN_BOOK = registerItem("book_of_patterns", new PatternBookItem(new FabricItemSettings().maxCount(1)));
    public static final Item KHIPU = registerItem("khipu", new KhipuItem(new FabricItemSettings().maxCount(1)));


    public static final Item BOBBIN_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "bobbin"),
            new BlockItem(BOBBIN, new FabricItemSettings())
    );

   public static final Item CONTROLLER_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "grand_loom"),
            new BlockItem(RITUAL_CONTROLLER, new FabricItemSettings())
    );

 public static final Item STARSTONE_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "starstone"),
            new BlockItem(STARSTONE, new FabricItemSettings())
    );

    public static final Item TRANSIT_ITEM = registerBlockItem("transit_relay", TRANSIT_RELAY);

    public static final Item SHEET_MUSIC = registerItem("sheet_music", new NoteHolderItem(new FabricItemSettings().maxCount(1)));

    public static final Item MUSIC_STAND_ITEM = registerBlockItem("music_stand", MUSIC_STAND);
    public static final Item KHIPU_HOOK_ITEM = registerBlockItem("khipu_hook", KHIPU_HOOK);





    public static void registerItems(){
        Songweaver.LOGGER.info("Registering Items for Loom");
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Songweaver.MOD_ID, name), item); }

    public static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier("songweaver", name), new BlockItem(block, new FabricItemSettings()));
    }
}
