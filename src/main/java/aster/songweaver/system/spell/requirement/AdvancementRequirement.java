package aster.songweaver.system.spell.requirement;

import aster.songweaver.system.ritual.RitualControllerBlockEntity;
import aster.songweaver.system.spell.definition.CastFailure;
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
    public CastFailure check(ServerPlayerEntity caster, @Nullable RitualControllerBlockEntity controller) {
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
