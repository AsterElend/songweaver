package aster.songweaver.datagen;

import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.registry.physical.LoomItems;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class LoomBlockLootProvider extends FabricBlockLootTableProvider {

    protected LoomBlockLootProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(LoomBlockStuff.STARSTONE);
        addDrop(LoomBlockStuff.TRANSIT_RELAY);
        addDrop(LoomBlockStuff.MUSIC_STAND);
        addDrop(LoomBlockStuff.KHIPU_HOOK);
        addDrop(LoomBlockStuff.BOBBIN);
        addDrop(LoomBlockStuff.FRACTAL_SAPLING);
        addDrop(LoomBlockStuff.FRACTAL_LEAVES, leavesDrops(LoomBlockStuff.FRACTAL_LEAVES, LoomBlockStuff.FRACTAL_SAPLING, 0.0025F));
    }
}
