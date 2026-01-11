package aster.songweaver.items;


import aster.songweaver.system.definition.Note;
import aster.songweaver.system.cast.SongServerCasting;
import aster.songweaver.client.InputBuffer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Distaff extends Item {

    public Distaff(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        // Server side: do nothing, let client handle intent
        if (!world.isClient) {
            return TypedActionResult.pass(stack);
        }

        // Only cast from main hand
        if (hand != Hand.MAIN_HAND) {
            return TypedActionResult.pass(stack);
        }

        // No notes → no cast
        if (InputBuffer.isEmpty()) {
            return TypedActionResult.pass(stack);
        }

        // Build packet
        PacketByteBuf buf = PacketByteBufs.create();
        var notes = InputBuffer.snapshot();

        buf.writeVarInt(notes.size());
        for (Note note : notes) {
            buf.writeEnumConstant(note);
        }

        // Send to server
        ClientPlayNetworking.send(SongServerCasting.CAST_DRAFT_PACKET, buf);


        // Clear client buffer immediately (OK for now)
        InputBuffer.clear();

        // Consume use (no vanilla behavior)
        return TypedActionResult.consume(stack);
    }
}



