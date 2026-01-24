package aster.songweaver.system.spell.requirement;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Requirement;
import aster.songweaver.system.spell.definition.Tier;
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
