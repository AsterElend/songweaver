package aster.songweaver.datagen;

import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class LoomModelProvider extends FabricModelProvider {
    public LoomModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator Bgenerator) {
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.STARSTONE);
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.VOIDSTONE);
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.TRANSIT_RELAY);
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.FRACTAL_PLANKS);
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.FRACTAL_LEAVES);
        Bgenerator.registerLog(LoomBlockStuff.FRACTAL_LOG).log(LoomBlockStuff.FRACTAL_LOG).wood(LoomBlockStuff.FRACTAL_WOOD);
        Bgenerator.registerLog(LoomBlockStuff.STRIPPED_FRACTAL_LOG).log(LoomBlockStuff.STRIPPED_FRACTAL_LOG).wood(LoomBlockStuff.STRIPPED_FRACTAL_WOOD);
        Bgenerator.registerTintableCross(LoomBlockStuff.FRACTAL_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);

         Bgenerator.registerSimpleCubeAll(LoomBlockStuff.WATCHER_PLANKS);
        Bgenerator.registerSimpleCubeAll(LoomBlockStuff.WATCHER_LEAVES);
        Bgenerator.registerLog(LoomBlockStuff.WATCHER_LOG).log(LoomBlockStuff.WATCHER_LOG).wood(LoomBlockStuff.WATCHER_WOOD);
        Bgenerator.registerLog(LoomBlockStuff.STRIPPED_WATCHER_LOG).log(LoomBlockStuff.STRIPPED_WATCHER_LOG).wood(LoomBlockStuff.STRIPPED_WATCHER_WOOD);
        Bgenerator.registerTintableCross(LoomBlockStuff.WATCHER_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);




    }

    @Override
    public void generateItemModels(ItemModelGenerator Igenerator) {
        Igenerator.register(LoomItems.DISTAFF_BASIC, Models.HANDHELD_ROD);
        Igenerator.register(LoomItems.DISTAFF_IRON, Models.HANDHELD_ROD);
        Igenerator.register(LoomItems.DISTAFF_DIAMOND, Models.HANDHELD_ROD);
        Igenerator.register(LoomItems.DISTAFF_NETHERITE, Models.HANDHELD_ROD);
        Igenerator.register(LoomItems.PATTERN_BOOK, Models.GENERATED);
        Igenerator.register(LoomItems.SHEET_MUSIC, Models.GENERATED);
        Igenerator.register(LoomItems.KHIPU, Models.GENERATED);
        Igenerator.register(LoomItems.SPINDLE, Models.GENERATED);
        Igenerator.register(LoomItems.BAGUETTE_MAGIQUE, Models.HANDHELD_ROD);
        Igenerator.register(LoomItems.THREAD, Models.GENERATED);


    }
}
