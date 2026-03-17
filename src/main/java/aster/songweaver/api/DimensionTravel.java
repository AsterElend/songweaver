package aster.songweaver.api;

import aster.songweaver.cca.SongweaverComponents;
import aster.songweaver.registry.physical.LoomMiscRegistry;
import aster.songweaver.registry.status.UnravelingEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import aster.songweaver.registry.physical.LoomItems;

import java.util.Set;

public class DimensionTravel {

    public static void enterHighWilderness(PlayerEntity player){
        ItemStack thread = new ItemStack(LoomItems.THREAD);

        NbtCompound tag = thread.getOrCreateNbt();

        tag.putInt("x", player.getBlockPos().getX());
        tag.putInt("y", player.getBlockPos().getY());
        tag.putInt("z", player.getBlockPos().getZ());
        tag.putString("dim", player.getWorld().getRegistryKey().getValue().toString());

        player.giveItemStack(thread);

        player.addStatusEffect(new StatusEffectInstance(
                LoomMiscRegistry.UNRAVELING,
                UnravelingEffect.BASE_DURATION,
                0
        ));

        SongweaverComponents.UNRAVELING_STATE.get(player).setClearable(false);
    }

    public static void leaveHighWilderness(LivingEntity player){
        SongweaverComponents.UNRAVELING_STATE.get(player).setClearable(true);
        player.removeStatusEffect(LoomMiscRegistry.UNRAVELING);
    }
}
