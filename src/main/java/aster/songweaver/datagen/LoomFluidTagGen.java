package aster.songweaver.datagen;

import aster.songweaver.registry.LoomTags;
import aster.songweaver.registry.physical.LoomFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LoomFluidTagGen extends FabricTagProvider<Fluid> {
    public LoomFluidTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.FLUID, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(LoomTags.LETHE).add(LoomFluids.LETHEAN_WATER_STATIC, LoomFluids.LETHEAN_WATER_FLOWING);
    }
}
