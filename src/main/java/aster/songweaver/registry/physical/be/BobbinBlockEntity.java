package aster.songweaver.registry.physical.be;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.api.packetry.RitualSiphonPacket;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class BobbinBlockEntity extends PedestalLikeBlockEntity {
    private int color;
    public BobbinBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.BOBBIN_ENTITY, pos, state);
        Random random = Random.create(pos.asLong());
        this.color = random.nextBetweenExclusive(0, 15);

    }

    public int removeCountForRitual(int amount, BlockPos controllerPos) {
        ItemStack removed = Inventories.splitStack(items, 0, amount);
        if (!removed.isEmpty()) {


            // Send visual packet to all nearby clients
            if (world instanceof ServerWorld serverWorld) {
                PacketByteBuf buf = PacketByteBufs.create();
                RitualSiphonPacket.write(buf, this.pos, controllerPos, Registries.ITEM.getId(removed.getItem()), removed.getCount(), this.color);

                PlayerLookup.around(serverWorld, Vec3d.ofCenter(this.pos), 64)
                        .forEach(player -> ServerPlayNetworking.send(player, RitualSiphonPacket.ID, buf));
            }
        }
        inventoryChanged();
        return removed.getCount();
    }

   @Override
    public void storeAdditionalData(NbtCompound nbt){
        nbt.putInt("colorIndex", color);
   }

   @Override
    public void readAdditionalData(NbtCompound nbt){
        color = nbt.getInt("colorIndex");
   }

   public int getColor(){
        return color;
   }

   public void setColor(int fresh){
        this.color = fresh;
   }
}
