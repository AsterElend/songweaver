package aster.songweaver.registry.dimension;

import aster.songweaver.Songweaver;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class LoomDimensions {
    public static final RegistryKey<DimensionOptions> HIGH_WILDERNESS_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            Songweaver.locate("high_wilderness"));
    public static final RegistryKey<World> HIGH_WILDERNESS_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Songweaver.locate("high_wilderness"));

    public static final RegistryKey<DimensionType> HIGH_WILDERNESS_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Songweaver.locate("high_wilderness_type"));

    public static void boostrapType(Registerable<DimensionType> context){
        context.register(HIGH_WILDERNESS_TYPE, new DimensionType(   OptionalLong.of(0),
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
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0,0), 0)));

    }
}