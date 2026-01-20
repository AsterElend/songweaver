package aster.songweaver.registry;

import aster.songweaver.system.ritual.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


import static aster.songweaver.Songweaver.MOD_ID;

public class LoomMiscRegistry {

    public static final StatusEffect SONG_SILENCE =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "song_silence"),
                    new SilenceEffect()
            );

    public static RegistryKey<DamageType> FRAY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("songweaver:fray"));

    public static DamageSource fray(World world) {
        return new DamageSource(
                world.getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(LoomMiscRegistry.FRAY)
        );
    }



    public static final Block BOBBIN = registerBlock("bobbin", new BobbinBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));

    public static final Item BOBBIN_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "bobbin"),
            new BlockItem(BOBBIN, new FabricItemSettings())
    );



    //there should NOT be an item for this one, don't register it
public static final Block RITUAL_CONTROLLER = registerBlock("grand_loom", new RitualControllerBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));




    public static final BlockEntityType<BobbinBlockEntity> BOBBIN_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "bobbin_entity"),
                    FabricBlockEntityTypeBuilder.create(BobbinBlockEntity::new,
                            LoomMiscRegistry.BOBBIN).build(null));


    public static final BlockEntityType<RitualControllerBlockEntity> RITUAL_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "ritual_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualControllerBlockEntity::new,
                            LoomMiscRegistry.RITUAL_CONTROLLER).build(null));


    public static Block registerBlock(String path, Block block){
        return Registry.register(Registries.BLOCK, new Identifier("songweaver", path), block);
    }







    public static void init() {

    }
}
