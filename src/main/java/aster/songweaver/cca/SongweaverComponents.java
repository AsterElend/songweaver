package aster.songweaver.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class SongweaverComponents implements EntityComponentInitializer {

    public static ComponentKey<HaloComponent> HALO = ComponentRegistry.getOrCreate(new Identifier("songweaver", "halo"), HaloComponent.class);
    public static ComponentKey<AmberComponent> AMBER_STEPS = ComponentRegistry.getOrCreate(new Identifier("songweaver", "amber_steps"), AmberComponent.class);
    public static ComponentKey<UnravelingStateComponent> UNRAVELING_STATE = ComponentRegistry.getOrCreate(new Identifier("songweaver", "unraveling_state"), UnravelingStateComponent.class);
    public static ComponentKey<ForgottenAdvancementComponent> FORGOTTEN = ComponentRegistry.getOrCreate(new Identifier("songweaver", "forgotten"), ForgottenAdvancementComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HALO, HaloComponent::new, RespawnCopyStrategy.NEVER_COPY);
        registry.registerForPlayers(FORGOTTEN, ForgottenAdvancementComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(LivingEntity.class, AMBER_STEPS, AmberComponent::new);
        registry.registerForPlayers(UNRAVELING_STATE, UnravelingStateComponent::new, RespawnCopyStrategy.NEVER_COPY);

    }
}
