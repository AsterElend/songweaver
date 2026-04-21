package aster.songweaver.registry.physical.entity;

import aster.songweaver.registry.physical.be.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static aster.songweaver.Songweaver.MOD_ID;
import static aster.songweaver.registry.physical.LoomBlockStuff.*;

public class LoomBlockEntities {
    public static final BlockEntityType<BobbinBlockEntity> BOBBIN_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "bobbin_entity"),
                    FabricBlockEntityTypeBuilder.create(BobbinBlockEntity::new,
                            BOBBIN).build(null));


    public static final BlockEntityType<GrandLoomBlockEntity> GRAND_LOOM_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "grand_loom_block_entity"),
                    FabricBlockEntityTypeBuilder.create(GrandLoomBlockEntity::new,
                            GRAND_LOOM).build(null));




    public static final BlockEntityType<MusicStandBlockEntity> MUSIC_STAND_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "music_stand_entity"),
                    FabricBlockEntityTypeBuilder.create(MusicStandBlockEntity::new,
                            MUSIC_STAND).build(null));



    public static final BlockEntityType<KhipuHookBlockEntity> KHIPU_HOOK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "khipu_entity"),
                    FabricBlockEntityTypeBuilder.create(KhipuHookBlockEntity::new,
                            KHIPU_HOOK).build(null));


    public static final BlockEntityType<RiftBlockEntity> RIFT_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "rift_entity"),
                    FabricBlockEntityTypeBuilder.create(RiftBlockEntity::new,
                            RIFT_BLOCK).build(null));



}
