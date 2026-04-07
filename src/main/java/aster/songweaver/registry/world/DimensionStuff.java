package aster.songweaver.registry.world;

import aster.songweaver.Songweaver;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DimensionStuff {
    public static final Identifier DIMENSION_ID = Songweaver.locate("high_wilderness");
    public static final RegistryKey<World> DIMENSION_KEY = RegistryKey.of(RegistryKeys.WORLD, DIMENSION_ID);

    public static void init(){}
}
