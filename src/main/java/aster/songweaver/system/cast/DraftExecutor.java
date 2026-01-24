package aster.songweaver.system.cast;


import aster.songweaver.registry.MagicRegistry;
import aster.songweaver.system.spell.loaders.DraftReloadListener;
import aster.songweaver.util.ParticleHelper;
import aster.songweaver.system.spell.loaders.RitualReloadListener;
import aster.songweaver.system.spell.definition.*;
import aster.songweaver.system.spell.definition.Draft;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public final class DraftExecutor {

    private DraftExecutor() {}

    public static void cast(ServerPlayerEntity player, List<Note> notes){



        DraftDefinition spell =
                DraftReloadListener.matchForDraft(notes);

        RitualDefinition possibleRitual = RitualReloadListener.matchForRitual(notes);


        if (spell == null && possibleRitual == null) {
            SongServerCasting.sendFeedback(
                    player,
                    CastFeedback.UNKNOWN_SONG
            );
            return;
        } else if (spell == null) {
            SongServerCasting.sendFeedback(
                    player,
                    CastFeedback.INVALID_STRUCTURE
            );
            return;
        }



        cast(player, spell);
    }




    public static void cast(ServerPlayerEntity caster,
                            DraftDefinition spell) {

        ServerWorld world = caster.getServerWorld();


        // Spindle interception



        // Requirements phase
        for (Requirement req : spell.requirements()) {
            CastFeedback failure = req.check(caster, null, false);
            if (failure != null) {
                SongServerCasting.sendFeedback(
                        caster,
                        failure
                );
                return;
            }
        }

        // Draft resolution
        Draft draft = MagicRegistry.getDraft(spell.draftId());

        // Draft execution
        draft.cast(world, caster, spell.data());


        //singing
        CastPlayback.play(
               world,
                caster.getPos(),
                spell.pattern().notes()
        );

        int length = spell.pattern().notes().size();

        for (int i = 0; i < length + 1; i++){
            ParticleHelper.spawnRandomNoteParticle(world, caster.getBlockPos());

        }




        // Drawbacks
        for (Drawback drawback : spell.drawbacks()) {
            drawback.apply(caster);
        }
    }
}
