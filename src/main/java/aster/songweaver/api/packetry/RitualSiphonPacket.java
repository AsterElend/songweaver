package aster.songweaver.api.packetry;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record RitualSiphonPacket(BlockPos bobbinPos, BlockPos controllerPos, Identifier transferItem, int itemCount,
                                 int color) {
    public static final Identifier ID = new Identifier("songweaver", "ritual_siphon");


    public static void write(PacketByteBuf buf, BlockPos bobbinPos, BlockPos controllerPos, Identifier itemTransfer, int itemCount, int color) {
        buf.writeBlockPos(bobbinPos);
        buf.writeBlockPos(controllerPos);
        buf.writeIdentifier(itemTransfer);
        buf.writeInt(itemCount);
        buf.writeInt(color);

    }

    public static RitualSiphonPacket read(PacketByteBuf buf) {
        return new RitualSiphonPacket(
                buf.readBlockPos(),
                buf.readBlockPos(),
                buf.readIdentifier(),
                buf.readInt(),
                buf.readInt()
        );
    }
}
