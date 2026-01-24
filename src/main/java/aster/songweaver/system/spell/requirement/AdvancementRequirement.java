package aster.songweaver.system.spell.requirement;

import aster.songweaver.registry.physical.ritual.GrandLoomBlockEntity;
import aster.songweaver.system.spell.definition.CastFeedback;
import aster.songweaver.system.spell.definition.Requirement;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AdvancementRequirement implements Requirement {
    private final Identifier advancementId;

    public AdvancementRequirement(Identifier advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public CastFeedback check(ServerPlayerEntity caster, @Nullable GrandLoomBlockEntity controller, boolean ritual) {
        Advancement adv = caster.server
                .getAdvancementLoader()
                .get(advancementId);

        if (adv == null ||
                !caster.getAdvancementTracker().getProgress(adv).isDone()) {
            return CastFeedback.LACKING_KNOWLEDGE;
        }

        return null;
    }
}
