package aster.songweaver.registry.physical;

import aster.songweaver.Songweaver;
import aster.songweaver.api.GenericSpaceSapling;
import aster.songweaver.registry.physical.be.*;
import aster.songweaver.registry.physical.block.*;
import aster.songweaver.registry.world.trees.FractalSaplingGenerator;
import aster.songweaver.registry.world.trees.WatcherSaplingGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static aster.songweaver.Songweaver.MOD_ID;

public class LoomBlockStuff {
    public static List<Block> LOOM_BLOCKS = new ArrayList<>();


    public static final Block BOBBIN = registerBlock("bobbin", new BobbinBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block STARSTONE = registerBlock("starstone", new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK)));
    public static final Block VOIDSTONE = registerBlock("voidstone", new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE)));
    public static final Block RITUAL_CONTROLLER = registerBlock("grand_loom", new GrandLoomBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
    public static final Block MUSIC_STAND = registerBlock("music_stand", new MusicStandBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
    public static final Block KHIPU_HOOK = registerBlock("khipu_hook", new KhipuHookBlock(FabricBlockSettings.copyOf(Blocks.TRIPWIRE_HOOK)));
    public static final Block LIGHT_ORB = registerBlockWithNoItem("light_orb", new LightOrbBlock(FabricBlockSettings.copyOf(Blocks.LIGHT).luminance(15).breakInstantly().noCollision()));
    public static final Block RIFT_BLOCK = registerBlock("rift", new RiftBlock(FabricBlockSettings.copyOf(Blocks.LIGHT).luminance(15)));

    public static final Block FRACTAL_LOG = registerBlock("fractal_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_STEM)));
    public static final Block STRIPPED_FRACTAL_LOG = registerBlock("stripped_fractal_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_STEM)));
    public static final Block FRACTAL_WOOD = registerBlock("fractal_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_HYPHAE)));
    public static final Block STRIPPED_FRACTAL_WOOD = registerBlock("stripped_fractal_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_HYPHAE)));
    public static final Block FRACTAL_PLANKS=
            registerBlock("fractal_planks", new Block(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
    public static final Block FRACTAL_SAPLING = registerBlock("fractal_sapling", new GenericSpaceSapling(new FractalSaplingGenerator(),
            FabricBlockSettings.copyOf(Blocks.BIRCH_SAPLING)));

    public static final Block FRACTAL_LEAVES = registerBlock("fractal_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES)));




    public static final Block WATCHER_LOG = registerBlock("watcher_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_STEM)));
    public static final Block STRIPPED_WATCHER_LOG = registerBlock("stripped_watcher_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_STEM)));
    public static final Block WATCHER_WOOD = registerBlock("watcher_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_HYPHAE)));
    public static final Block STRIPPED_WATCHER_WOOD = registerBlock("stripped_watcher_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_HYPHAE)));
    public static final Block WATCHER_PLANKS=
            registerBlock("watcher_planks", new Block(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
    public static final Block WATCHER_SAPLING = registerBlock("watcher_sapling", new GenericSpaceSapling(new WatcherSaplingGenerator(),
            FabricBlockSettings.copyOf(Blocks.BIRCH_SAPLING)));

    public static final Block WATCHER_LEAVES = registerBlock("watcher_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES)));

   public static final Block LETHEAN_WATER = registerBlockWithNoItem("lethean_water", new LetheanWaterBlock(LoomFluids.LETHEAN_WATER, FabricBlockSettings.copyOf(Blocks.WATER)));








    public static Block registerBlock(String path, Block block){
        LOOM_BLOCKS.add(block);
        registerBlockItem(path, block);
        return Registry.register(Registries.BLOCK, new Identifier("songweaver", path), block);
    }



    public static Block registerBlockWithNoItem(String path, Block block){
        return Registry.register(Registries.BLOCK, Songweaver.locate(path), block);
    }


    public static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, new Identifier("songweaver", name), new BlockItem(block, new FabricItemSettings()));
    }

public static void init(){}
}
