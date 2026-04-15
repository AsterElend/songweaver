package aster.songweaver.registry.world.trees;

import aster.songweaver.Songweaver;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;

public class LoomPlacedFeatures {
    public static final RegistryKey<PlacedFeature> FRACTAL_TREE_PLACED = makeKey("fractal_tree_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

      //  register(context, FRACTAL_TREE_PLACED, configuredFeatureRegistryLookup.getOrThrow(LoomConfiguredFeatures.FRACTAL_TREE_KEY),
          //     morestuff);

        //todo fix this
    }

    public static RegistryKey<PlacedFeature> makeKey(String name){
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Songweaver.locate(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?,?>> config,
                                 List<PlacementModifier> modifiers){
        context.register(key, new PlacedFeature(config, List.copyOf(modifiers)));
    }
}
