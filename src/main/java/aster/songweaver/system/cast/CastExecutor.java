package aster.songweaver.system.cast;


import aster.songweaver.system.DraftReloadListener;
import aster.songweaver.system.ParticleHelper;
import aster.songweaver.system.spell.definition.*;
import aster.songweaver.registry.items.SpindleItem;
import aster.songweaver.registry.DraftRegistry;
import aster.songweaver.system.spell.definition.Draft;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

public final class CastExecutor {

    private CastExecutor() {}

    public static void cast(ServerPlayerEntity player, List<Note> notes){
        ItemStack offhand = player.getOffHandStack();

        if (offhand.getItem() instanceof SpindleItem) {

            SpindleItem.SpindleUtil.storeNotes(offhand, notes);

            player.sendMessage(
                    Text.literal("The spindle quietly takes up the song."),
                    true
            );

            return; //  DO NOT CAST
        }


        DraftDefinition spell =
                DraftReloadListener.matchForDraft(notes);

        if (spell == null) {
            SongServerCasting.sendFailure(
                    player,
                    CastFailure.UNKNOWN_SONG
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
            CastFailure failure = req.check(caster, null);
            if (failure != null) {
                SongServerCasting.sendFailure(
                        caster,
                        failure
                );
                return;
            }
        }

        // Draft resolution
        Draft draft = DraftRegistry.get(spell.draftId());

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
