package aster.songweaver.system.requirement;

import aster.songweaver.system.definition.CastFailure;
import aster.songweaver.system.definition.Requirement;
import com.google.gson.JsonObject;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DimensionRequirement implements Requirement {
    private final RegistryKey<World> dimension;

    public DimensionRequirement(String dimensionId) {
        this.dimension = RegistryKey.of(
                RegistryKeys.WORLD,
                new Identifier(dimensionId)
        );
    }

    @Override
    public CastFailure check(ServerPlayerEntity caster) {

        return checkDimension(caster.getServerWorld());

    }
    private CastFailure checkDimension(ServerWorld world) {
        return world.getRegistryKey() == dimension ? null : CastFailure.WRONG_DIMENSION;
    }

}
