package aster.songweaver.registry.physical.be;

import aster.songweaver.api.PedestalLikeBlockEntity;
import aster.songweaver.registry.physical.entity.LoomBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Locale;

public class BobbinBlockEntity extends PedestalLikeBlockEntity {
    public int color;
    public BobbinBlockEntity(BlockPos pos, BlockState state) {
        super(LoomBlockEntities.BOBBIN_ENTITY, pos, state);
        Random random = Random.create(pos.asLong());
        this.color = random.nextBetweenExclusive(0, 15);

    }

    public int removeCount(int amount) {
        ItemStack removed = Inventories.splitStack(items, 0, amount);
        if (!removed.isEmpty()) {
            inventoryChanged();
        }
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
}
