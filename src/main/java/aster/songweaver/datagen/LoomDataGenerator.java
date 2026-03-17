package aster.songweaver.datagen;

import aster.songweaver.registry.dimension.LoomDimensions;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.world.LoomConfiguredFeatures;
import aster.songweaver.registry.world.trees.LoomPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class LoomDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();


		pack.addProvider(LoomModelProvider::new);
		pack.addProvider(LoomItemTagGen::new);
		pack.addProvider(RecipeGenerator::new);
		pack.addProvider(LoomBlockLootProvider::new);
		pack.addProvider(LoomWorldGen::new);
		pack.addProvider(LoomBlockTagGen::new);


	}


	@Override
	public void buildRegistry(RegistryBuilder builder){
		builder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, LoomConfiguredFeatures::bootstrap);
		builder.addRegistry(RegistryKeys.PLACED_FEATURE, LoomPlacedFeatures::bootstrap);
		builder.addRegistry(RegistryKeys.CHUNK_GENERATOR_SETTINGS, LoomNoiseSettings::bootstrap);
		builder.addRegistry(RegistryKeys.DIMENSION_TYPE, LoomDimensions::bootstrapType);
		builder.addRegistry(RegistryKeys.BIOME, LoomDimensions::bootstrapBiome);
		builder.addRegistry(RegistryKeys.DIMENSION, LoomDimensions::bootstrapOptions);



	}
}
