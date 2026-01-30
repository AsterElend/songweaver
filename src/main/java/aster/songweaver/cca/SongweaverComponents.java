package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class SongweaverComponents implements EntityComponentInitializer {

    public static final ComponentKey<HaloComponent> HALO = ComponentRegistry.getOrCreate(Identifier.of("songweaver", "halo"), HaloComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerForPlayers(HALO, HaloComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
