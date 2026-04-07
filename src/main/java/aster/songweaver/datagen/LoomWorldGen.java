package aster.songweaver.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LoomWorldGen extends FabricDynamicRegistryProvider {


    public LoomWorldGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.DIMENSION_TYPE));
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME));
        entries.addAll(wrapperLookup.getWrapperOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS));




    }

    @Override
    public String getName() {
        return "World Gen";
    }
}
