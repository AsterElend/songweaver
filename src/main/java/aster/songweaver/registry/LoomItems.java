package aster.songweaver.registry;

import aster.songweaver.Songweaver;
import aster.songweaver.items.Distaff;
import aster.songweaver.items.SpindleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LoomItems {
    public static final Item DISTAFF_BASIC = registerItem("distaff_basic", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_IRON = registerItem("distaff_iron", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_DIAMOND = registerItem("distaff_diamond", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_NETHERITE = registerItem("distaff_netherite", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item DISTAFF_ASTRAL = registerItem("distaff_astral", new Distaff(new FabricItemSettings().maxCount(1)));
    public static final Item SPINDLE = registerItem("spindle", new SpindleItem(new FabricItemSettings().maxCount(1)));









    public static void registerItems(){
        Songweaver.LOGGER.info("Registering Items for Loom");
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Songweaver.MOD_ID, name), item); }
}
