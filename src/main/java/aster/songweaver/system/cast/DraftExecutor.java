package aster.songweaver.system.cast;


import aster.songweaver.api.SongweaverPackets;
import aster.songweaver.registry.MagicRegistry;
import aster.songweaver.system.spell.loaders.DraftReloadListener;
import aster.songweaver.util.ParticleHelper;
import aster.songweaver.system.spell.loaders.RitualReloadListener;
import aster.songweaver.api.weaving.*;
import aster.songweaver.api.weaving.Draft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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
            SongweaverPackets.sendFeedback(
                    player,
                    CastFeedback.UNKNOWN_SONG
            );
            return;
        } else if (spell == null) {
            SongweaverPackets.sendFeedback(
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
        PlayerInventory inventory = caster.getInventory();
        CastFeedback noComponents = CastFeedback.NO_COMPONENTS;
        for (ItemStack component: spell.components()){
            if (!checkComponent(component, inventory)){
                SongweaverPackets.sendFeedback(caster, noComponents);
                return;
            }
        }


        // Requirements phase
        for (Requirement req : spell.requirements()) {
            CastFeedback failure = req.check(caster, null, false);
            if (failure != null) {
                SongweaverPackets.sendFeedback(
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

        for (ItemStack stack: spell.components()){
            inventory.removeOne(stack);
        }
    }

    private static boolean checkComponent(ItemStack stack, PlayerInventory inv){
        int count = inv.count(stack.getItem());
        return count > stack.getCount();
    }
}
