package aster.songweaver.system.requirement;

import aster.songweaver.system.definition.CastFailure;
import aster.songweaver.system.definition.Requirement;
import aster.songweaver.system.definition.Tier;
import net.minecraft.server.network.ServerPlayerEntity;

public class TierRequirement implements Requirement {

    private final Tier required;

    public TierRequirement(Tier required) {
        this.required = required;
    }

    @Override
    public CastFailure check(ServerPlayerEntity caster) {
        Tier held = TierCheckHelper.getHeldTier(caster);

        if (held == null || !held.meets(required)) {
            return CastFailure.INSUFFICIENT_POWER;
        }

        return null;
    }
}
