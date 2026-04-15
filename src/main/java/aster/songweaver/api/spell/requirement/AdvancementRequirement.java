package aster.songweaver.api.spell.requirement;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.be.GrandLoomBlockEntity;
import aster.songweaver.api.weaving.CastFeedback;
import aster.songweaver.api.weaving.Requirement;
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
                !caster.getAdvancementTracker().getProgress(adv).isDone() ||
                SongweaverComponents.FORGOTTEN.get(caster).isForgotten(advancementId)) {
            return CastFeedback.LACKING_KNOWLEDGE;
        }

        return null;
    }
}
