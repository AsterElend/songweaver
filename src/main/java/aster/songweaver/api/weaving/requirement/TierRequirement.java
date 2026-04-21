package aster.songweaver.api.weaving.requirement;

import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Requirement;
import aster.songweaver.api.weaving.Tier;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.util.TierCheckHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class TierRequirement implements Requirement {

    private final Tier required;

    public TierRequirement(Tier required) {
        this.required = required;
    }

    @Override
    public CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual) {
        Tier held;

        if (ritual){
             held = controller.stockpiledTier;
        } else {
         held = TierCheckHelper.getHeldTier(caster);}

        if (held == null || !held.meets(required)) {
            return CastFeedback.INSUFFICIENT_POWER;
        }

        return null;
    }
}
