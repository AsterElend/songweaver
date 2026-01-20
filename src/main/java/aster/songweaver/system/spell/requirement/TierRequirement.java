package aster.songweaver.system.spell.requirement;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
import aster.songweaver.system.spell.definition.Requirement;
import aster.songweaver.system.spell.definition.Tier;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class TierRequirement implements Requirement {

    private final Tier required;

    public TierRequirement(Tier required) {
        this.required = required;
    }

    @Override
    public CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller) {
        Tier held = TierCheckHelper.getHeldTier(caster);

        if (held == null || !held.meets(required)) {
            return CastFailure.INSUFFICIENT_POWER;
        }

        return null;
    }
}
