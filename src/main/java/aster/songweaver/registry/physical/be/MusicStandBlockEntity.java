package aster.songweaver.registry.physical.be;

import aster.songweaver.api.ImplementedInventory;
import aster.songweaver.api.NoteHolderItem;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.LoomBlockStuff;
import aster.songweaver.system.spell.definition.Note;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MusicStandBlockEntity extends PedestalLikeBlockEntity {



    public MusicStandBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockStuff.MUSIC_STAND_ENTITY, pos, state);
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
