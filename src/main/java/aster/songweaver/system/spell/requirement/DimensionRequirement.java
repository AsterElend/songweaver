package aster.songweaver.system.spell.requirement;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Requirement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DimensionRequirement implements Requirement {
    private final RegistryKey<World> dimension;

    public DimensionRequirement(String dimensionId) {
        this.dimension = RegistryKey.of(
                RegistryKeys.WORLD,
                new Identifier(dimensionId)
        );
    }

    @Override
    public CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller) {

        return checkDimension(caster.getServerWorld());

    }

    private CastFailure checkDimension(ServerWorld world) {
        return world.getRegistryKey() == dimension ? null : CastFailure.WRONG_DIMENSION;
    }

}
