package aster.songweaver.system.cast;


import aster.songweaver.system.DraftReloadListener;
import aster.songweaver.system.definition.*;
import aster.songweaver.items.SpindleItem;
import aster.songweaver.registry.DraftRegistry;
import aster.songweaver.registry.LoomMiscRegistry;
import aster.songweaver.system.definition.Draft;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

public final class CastExecutor {

    private CastExecutor() {}

    public static void cast(ServerPlayerEntity player, List<Note> notes){
        if (player.hasStatusEffect(LoomMiscRegistry.SONG_SILENCE)) {
            SongServerCasting.sendFailure(
                    player,
                    CastFailure.SILENCED
            );
            return;
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
        ItemStack offhand = caster.getOffHandStack();

        if (offhand.getItem() instanceof SpindleItem
                && !SpindleItem.SpindleUtil.hasNotes(offhand)) {

            SpindleItem.SpindleUtil.storeNotes(offhand, spell.pattern().notes());

            caster.sendMessage(
                    Text.literal("The spindle quietly takes up the song."),
                    true
            );

            return; //  DO NOT CAST
        }


        // Requirements phase
        for (Requirement req : spell.requirements()) {
            CastFailure failure = req.check(caster);
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
        if (draft == null) {
            SongServerCasting.sendFailure(
                    caster,
                    CastFailure.UNKNOWN_SONG
            );
            return;
        }

        // Draft execution
        draft.cast(world, caster, spell.data());


        //singing
        CastPlayback.play(
               world,
                caster.getPos(),
                spell.pattern().notes()
        );


        // Drawbacks
        for (Drawback drawback : spell.drawbacks()) {
            drawback.apply(caster);
        }
    }
}
