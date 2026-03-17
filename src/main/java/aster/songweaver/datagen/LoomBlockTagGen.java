package aster.songweaver.datagen;

import aster.songweaver.registry.physical.LoomBlockStuff;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class LoomBlockTagGen extends FabricTagProvider.BlockTagProvider {
    public LoomBlockTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.LOGS).add(LoomBlockStuff.FRACTAL_LOG).add(LoomBlockStuff.WATCHER_LOG);
        getOrCreateTagBuilder(BlockTags.LEAVES).add(LoomBlockStuff.FRACTAL_LEAVES).add(LoomBlockStuff.WATCHER_LEAVES);
        getOrCreateTagBuilder(BlockTags.SAPLINGS).add(LoomBlockStuff.FRACTAL_SAPLING).add(LoomBlockStuff.WATCHER_SAPLING);
    }
}
