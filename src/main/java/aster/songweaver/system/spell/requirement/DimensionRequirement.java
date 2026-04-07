package aster.songweaver.system.spell.requirement;

import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Requirement;
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
    public CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual) {
        if (ritual){
            return checkDimension((ServerWorld) controller.getWorld());
        }

        return checkDimension(caster.getServerWorld());

    }

    private CastFeedback checkDimension(ServerWorld world) {
        return world.getRegistryKey() == dimension ? null : CastFeedback.WRONG_DIMENSION;
    }

}
