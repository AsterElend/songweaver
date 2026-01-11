package aster.songweaver.registry;

import aster.songweaver.Songweaver;
import aster.songweaver.ritual.BobbinBlock;
import aster.songweaver.ritual.BobbinBlockEntity;
import aster.songweaver.ritual.RitualControllerBlock;
import aster.songweaver.ritual.RitualControllerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LoomMiscRegistry {

    public static final StatusEffect SONG_SILENCE =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "song_silence"),
                    new SilenceEffect()
            );



public static final Block BOBBIN = registerBlock("bobbin", new BobbinBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
public static final Block RITUAL_CONTROLLER = registerBlock("ritual_controller", new RitualControllerBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));




    public static final BlockEntityType<BobbinBlockEntity> BOBBIN_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Songweaver.MOD_ID, "bobbin_entity"),
                    FabricBlockEntityTypeBuilder.create(BobbinBlockEntity::new,
                            LoomMiscRegistry.BOBBIN).build(null));


    public static final BlockEntityType<RitualControllerBlockEntity> RITUAL_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Songweaver.MOD_ID, "ritual_entity"),
                    FabricBlockEntityTypeBuilder.create(RitualControllerBlockEntity::new,
                            LoomMiscRegistry.RITUAL_CONTROLLER).build(null));


    public static Block registerBlock(String path, Block block){
        return Registry.register(Registries.BLOCK, new Identifier("songweaver", path), block);
    }







    public static void init() {

    }
}
