package aster.songweaver.registry.physical;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.physical.block.LetheanFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LoomFluids {
    public static final FlowableFluid LETHEAN_WATER_STATIC = Registry.register(Registries.FLUID, Songweaver.locate("lethean_water_static"), new LetheanFluid.Still());
    public static final FlowableFluid LETHEAN_WATER_FLOWING = Registry.register(Registries.FLUID, Songweaver.locate("lethean_water_flowing"), new LetheanFluid.Flowing());

    public static void invoke(){

    }
}
