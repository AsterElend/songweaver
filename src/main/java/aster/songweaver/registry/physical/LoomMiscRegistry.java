package aster.songweaver.registry.physical;

import aster.songweaver.Songweaver;
import aster.songweaver.registry.physical.entity.LightOrbProjectileEntity;
import aster.songweaver.registry.status.AmberStepsEffect;
import aster.songweaver.registry.status.LightfootEffect;
import aster.songweaver.registry.status.SilenceEffect;
import aster.songweaver.registry.status.UnravelingEffect;
import aster.songweaver.registry.world.ArbitraryGeodeFeature;
import aster.songweaver.registry.world.ArbitraryGeodeFeatureConfig;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
import net.minecraft.world.gen.feature.Feature;

public class LoomMiscRegistry {



    public static final StatusEffect SONG_SILENCE =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "song_silence"),
                    new SilenceEffect()
            );


      public static final StatusEffect AMBER_STEPS =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "amber_steps"),
                    new AmberStepsEffect()
            );


      public static final StatusEffect LIGHTFOOT =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "lightfoot"),
                    new LightfootEffect()
            );


  public static final StatusEffect UNRAVELING =
            Registry.register(
                    Registries.STATUS_EFFECT,
                    new Identifier("songweaver", "unraveling"),
                    new UnravelingEffect()
            );




    public static RegistryKey<DamageType> FRAY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("songweaver:fray"));

    public static DamageSource fray(World world) {
        return new DamageSource(
                world.getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(LoomMiscRegistry.FRAY)
        );
    }

    public static final EntityType<LightOrbProjectileEntity> LIGHT_ORB_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("songweaver:light_orb_projectile"),
            FabricEntityTypeBuilder
                    .create(SpawnGroup.MISC, LightOrbProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4)
                    .build()
    );

    public static final Feature<ArbitraryGeodeFeatureConfig> ARBITRARY_GEODE = Registry.register(
            Registries.FEATURE,
            Songweaver.locate("arbitrary_geode"),
            new ArbitraryGeodeFeature()
    );


    public static void init() {

    }
}
