package aster.songweaver.registry.physical;

import aster.songweaver.registry.SilenceEffect;
import aster.songweaver.registry.physical.ritual.*;
import aster.songweaver.cca.HaloComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
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
    public static final Block STARSTONE = registerBlock("starstone", new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK)));
    public static final Block TRANSIT_RELAY = registerBlock("transit_relay", new TransitRelay(FabricBlockSettings.copyOf(Blocks.CAULDRON)));
public static final Block RITUAL_CONTROLLER = registerBlock("grand_loom", new GrandLoomBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
public static final Block MUSIC_STAND = registerBlock("music_stand", new MusicStandBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS)));
public static final Block KHIPU_HOOK = registerBlock("khipu_hook", new KhipuHookBlock(FabricBlockSettings.copyOf(Blocks.TRIPWIRE_HOOK)));
public static final Block LIGHT_ORB = registerBlock("light_orb", new LightOrbBlock(FabricBlockSettings.copyOf(Blocks.LIGHT).luminance(15).breakInstantly()));

public static final EntityType<LightOrbProjectileEntity> LIGHT_ORB_PROJECTILE = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("songweaver:light_orb_projectile"),
        FabricEntityTypeBuilder
                .create(SpawnGroup.MISC, LightOrbProjectileEntity::new)
                .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                .trackRangeBlocks(4)
                .build()
);



    public static final BlockEntityType<BobbinBlockEntity> BOBBIN_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "bobbin_entity"),
                    FabricBlockEntityTypeBuilder.create(BobbinBlockEntity::new,
                            LoomMiscRegistry.BOBBIN).build(null));


    public static final BlockEntityType<GrandLoomBlockEntity> RITUAL_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "ritual_entity"),
                    FabricBlockEntityTypeBuilder.create(GrandLoomBlockEntity::new,
                            LoomMiscRegistry.RITUAL_CONTROLLER).build(null));




    public static final BlockEntityType<MusicStandBlockEntity> MUSIC_STAND_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "music_stand_entity"),
                    FabricBlockEntityTypeBuilder.create(MusicStandBlockEntity::new,
                            LoomMiscRegistry.MUSIC_STAND).build(null));



    public static final BlockEntityType<KhipuHookBlockEntity> KHIPU_HOOK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "khipu_entity"),
                    FabricBlockEntityTypeBuilder.create(KhipuHookBlockEntity::new,
                            LoomMiscRegistry.KHIPU_HOOK).build(null));







    public static Block registerBlock(String path, Block block){
        return Registry.register(Registries.BLOCK, new Identifier("songweaver", path), block);
    }







    public static void init() {

    }
}
