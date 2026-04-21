package aster.songweaver.registry.world.trees;

import aster.songweaver.Songweaver;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class LoomTrunkPlacers {
    public static final TrunkPlacerType<FractalTreeTrunkPlacer> FRACTAL_TRUNK =
            Registry.register(
                    Registries.TRUNK_PLACER_TYPE,
                    Songweaver.locate("fractal_trunk"),
                    new TrunkPlacerType<>(FractalTreeTrunkPlacer.CODEC)
            );

    public static void init(){}
}
