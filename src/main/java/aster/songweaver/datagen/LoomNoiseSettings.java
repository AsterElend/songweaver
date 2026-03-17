package aster.songweaver.datagen;

import aster.songweaver.registry.dimension.LoomDimensions;
import aster.songweaver.registry.physical.LoomBlockStuff;
import com.mojang.serialization.Lifecycle;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import java.util.List;

public class LoomNoiseSettings {

    public static void bootstrap(Registerable<ChunkGeneratorSettings> context) {



        DensityFunction zero = DensityFunctionTypes.constant(0);

        DensityFunction baseDensity =
                DensityFunctionTypes.yClampedGradient(
                        0,     // from Y
                        128,   // to Y
                        1.0,
                        -1.0
                );

        NoiseRouter router = new NoiseRouter(
                zero, // barrier
                zero, // fluidLevelFloodedness
                zero, // fluidLevelSpread
                zero, // lava
                zero, // temperature
                zero, // vegetation
                zero, // continents
                zero, // erosion
                zero, // depth
                zero, // ridges
                baseDensity, // initial density
                baseDensity, // final density
                zero, // veinToggle
                zero, // veinRidged
                zero  // veinGap
        );
        MaterialRules.MaterialRule surfaceRule =
                MaterialRules.sequence(

                        MaterialRules.condition(
                                MaterialRules.verticalGradient(
                                        "starstone_floor",
                                        YOffset.BOTTOM,
                                        YOffset.aboveBottom(5)
                                ),
                                MaterialRules.block(LoomBlockStuff.STARSTONE.getDefaultState())
                        )

                );

        ChunkGeneratorSettings settings = new ChunkGeneratorSettings(
                new GenerationShapeConfig(
                        0,     // minY
                        256,   // height
                        1,     // horizontal size
                        2      // vertical size
                ),
                Blocks.AIR.getDefaultState(), // default fluid
                LoomBlockStuff.VOIDSTONE.getDefaultState(), // default block
                router,
                surfaceRule,
                List.of(), // spawn targets
                0, // sea level
                false, // disable mob gen
                false, // aquifers
                false, // ore veins
                false  // legacy random
        );

        context.register(LoomDimensions.ROLLING_HILLS, settings, Lifecycle.stable());
    }
}
