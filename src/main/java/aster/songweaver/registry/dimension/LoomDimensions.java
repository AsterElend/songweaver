package aster.songweaver.registry.dimension;

import aster.songweaver.Songweaver;
import aster.songweaver.datagen.LoomNoiseSettings;
import aster.songweaver.registry.physical.LoomBlockStuff;
import com.mojang.serialization.Lifecycle;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;

import java.util.List;
import java.util.OptionalLong;

public class LoomDimensions {

    public static final RegistryKey<DimensionOptions> HIGH_WILDERNESS_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Songweaver.locate("high_wilderness"));


 public static final RegistryKey<World> HIGH_WILDERNESS = RegistryKey.of(RegistryKeys.WORLD,
            Songweaver.locate("high_wilderness"));



    public static final RegistryKey<DimensionType> HIGH_WILDERNESS_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Songweaver.locate("high_wilderness_type"));
    public static final RegistryKey<ChunkGeneratorSettings> ROLLING_HILLS = RegistryKey.of(RegistryKeys.CHUNK_GENERATOR_SETTINGS,
            Songweaver.locate("rolling_hills"));

    public static final RegistryKey<Biome> VOIDSTONE_HILLS = RegistryKey.of(RegistryKeys.BIOME,
            Songweaver.locate("rolling_voidstone"));

 


    public static void bootstrapType(Registerable<DimensionType> context){
        context.register(HIGH_WILDERNESS_TYPE, new DimensionType( OptionalLong.of(0),
                true,
                false,
                false,
                false,
                6.0,
                false,
                true,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                DimensionTypes.OVERWORLD_ID,
                7f,
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0,0), 0)), Lifecycle.stable());

    }

    public static void bootstrapBiome(Registerable<Biome> context){

        SpawnSettings spawnSettings = new SpawnSettings.Builder().build();

        GenerationSettings generationSettings =
                new GenerationSettings.LookupBackedBuilder(
                        context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER)
                ).build();

        context.register(VOIDSTONE_HILLS, new Biome.Builder()
                .temperature(0.0f)
                .downfall(0.0f)
                .precipitation(false)
                .effects(new BiomeEffects.Builder()
                        .skyColor(0)
                        .fogColor(0)
                        .waterColor(2709069)
                        .waterFogColor(3166261)
                        .grassColor(16711680)
                        .foliageColor(8421631)
                        .build())
                .spawnSettings(spawnSettings)
                .generationSettings(generationSettings)
                .build(), Lifecycle.stable());
    }





    public static void bootstrapOptions(Registerable<DimensionOptions> context) {

        RegistryEntryLookup<DimensionType> dimTypes =
                context.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);

        RegistryEntryLookup<ChunkGeneratorSettings> noise =
                context.getRegistryLookup(RegistryKeys.CHUNK_GENERATOR_SETTINGS);

        RegistryEntry<ChunkGeneratorSettings> noiseSettings =
                noise.getOrThrow(ROLLING_HILLS);

        RegistryEntryLookup<Biome> biomes =
                context.getRegistryLookup(RegistryKeys.BIOME);

        RegistryEntry<DimensionType> type =
                dimTypes.getOrThrow(HIGH_WILDERNESS_TYPE);

        ChunkGenerator generator = new NoiseChunkGenerator(
                new FixedBiomeSource(biomes.getOrThrow(VOIDSTONE_HILLS)),
                noiseSettings
        );

        context.register(HIGH_WILDERNESS_KEY, new DimensionOptions(type, generator), Lifecycle.stable());
    }


}