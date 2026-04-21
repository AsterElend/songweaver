package aster.songweaver.registry.physical.be;

import aster.songweaver.api.NoteHolderItem;
import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.api.weaving.Note;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class MusicStandBlockEntity extends PedestalLikeBlockEntity {



    public MusicStandBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.MUSIC_STAND_ENTITY, pos, state);
    }

    /** Called by your ritual system */
    public List<Note> getNotes() {
        if (items.isEmpty()) return List.of();
        return NoteHolderItem.NotestoreUtil.readNotes(items.get(0));
    }

    public boolean hasNotes() {
        return !items.isEmpty() && !NoteHolderItem.NotestoreUtil.notHasNotes(items.get(0));
    }




}
