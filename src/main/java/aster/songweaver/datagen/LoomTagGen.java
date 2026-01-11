package aster.songweaver.datagen;

import aster.songweaver.registry.LoomItems;
import aster.songweaver.registry.LoomTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LoomTagGen extends FabricTagProvider<Item> {
    public LoomTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(LoomTags.DISTAFFS).add(LoomItems.DISTAFF_BASIC, LoomItems.DISTAFF_IRON, LoomItems.DISTAFF_DIAMOND, LoomItems.DISTAFF_NETHERITE, LoomItems.DISTAFF_ASTRAL);

    }
}

// ...

