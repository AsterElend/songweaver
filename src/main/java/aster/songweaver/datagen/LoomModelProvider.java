package aster.songweaver.datagen;

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
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(LoomMiscRegistry.STARSTONE);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(LoomItems.DISTAFF_BASIC, Models.HANDHELD_ROD);
        itemModelGenerator.register(LoomItems.DISTAFF_IRON, Models.HANDHELD_ROD);
        itemModelGenerator.register(LoomItems.DISTAFF_DIAMOND, Models.HANDHELD_ROD);
        itemModelGenerator.register(LoomItems.DISTAFF_NETHERITE, Models.HANDHELD_ROD);
        itemModelGenerator.register(LoomItems.PATTERN_BOOK, Models.GENERATED);
        itemModelGenerator.register(LoomItems.SHEET_MUSIC, Models.GENERATED);
        itemModelGenerator.register(LoomItems.KHIPU, Models.GENERATED);

    }
}
