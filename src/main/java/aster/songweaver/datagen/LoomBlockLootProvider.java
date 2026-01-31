package aster.songweaver.datagen;

import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class LoomBlockLootProvider extends FabricBlockLootTableProvider {

    protected LoomBlockLootProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(LoomMiscRegistry.STARSTONE);
        addDrop(LoomMiscRegistry.TRANSIT_RELAY);
        addDrop(LoomMiscRegistry.MUSIC_STAND);
        addDrop(LoomMiscRegistry.KHIPU_HOOK);
        addDrop(LoomMiscRegistry.BOBBIN);
    }
}
