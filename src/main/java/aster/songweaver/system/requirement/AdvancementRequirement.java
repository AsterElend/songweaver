package aster.songweaver.system.requirement;

import aster.songweaver.system.definition.CastFailure;
import aster.songweaver.system.definition.Requirement;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AdvancementRequirement implements Requirement {
    private final Identifier advancementId;

    public AdvancementRequirement(Identifier advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public CastFailure check(ServerPlayerEntity caster) {
        Advancement adv = caster.server
                .getAdvancementLoader()
                .get(advancementId);

        if (adv == null ||
                !caster.getAdvancementTracker().getProgress(adv).isDone()) {
            return CastFailure.LACKING_KNOWLEDGE;
        }

        return null;
    }
}
