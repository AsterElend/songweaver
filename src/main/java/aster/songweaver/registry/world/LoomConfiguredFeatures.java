package aster.songweaver.registry.world;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.world.trees.FractalFoliagePlacer;
import aster.songweaver.registry.world.trees.FractalTreeTrunkPlacer;
import net.minecraft.block.LoomBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class LoomConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> FRACTAL_TREE_KEY = registerKey("fractal_tree");


    public static void bootstrap(Registerable<ConfiguredFeature<?,?>> context){
        register(context, FRACTAL_TREE_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(LoomBlockStuff.FRACTAL_LOG),
                new FractalTreeTrunkPlacer(2, 1, 0),
                BlockStateProvider.of(LoomBlockStuff.FRACTAL_LEAVES),
                new FractalFoliagePlacer(ConstantIntProvider.create(4), ConstantIntProvider.create(1)),
                new TwoLayersFeatureSize(1, 0, 2)).build());
    }











public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name){
    return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Songweaver.locate(name));
}

private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?,?>> context,
                                                                               RegistryKey<ConfiguredFeature<?, ?>> key,
                                                                               F feature,
                                                                               FC configuration){
    context.register(key, new ConfiguredFeature<>(feature, configuration));
}


}
